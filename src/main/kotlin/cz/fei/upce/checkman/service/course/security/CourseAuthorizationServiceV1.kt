package cz.fei.upce.checkman.service.course.security

import cz.fei.upce.checkman.domain.course.AppUserCourseSemesterRole
import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.domain.course.CourseSemesterAccessRequest
import cz.fei.upce.checkman.domain.course.CourseSemesterAccessRequest.Companion.EXPIRATION
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.graphql.output.course.CourseSemesterAccessRequestQL
import cz.fei.upce.checkman.repository.course.CourseSemesterRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import cz.fei.upce.checkman.service.course.CourseServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.fei.upce.checkman.service.course.security.exception.AppUserCanAlreadyAccessSemesterException
import cz.fei.upce.checkman.service.course.security.exception.CourseSemesterAlreadyEndedException
import cz.fei.upce.checkman.service.course.security.exception.CourseSemesterNotStartedYetException
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.relational.core.query.Query.query
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.isEqual
import org.springframework.data.relational.core.query.isIn
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime

@Service
class CourseAuthorizationServiceV1(
    private val courseSemesterRepository: CourseSemesterRepository,
    private val cacheFactory: ReactiveRedisConnectionFactory,
    private val courseAccessOps: ReactiveRedisOperations<String, CourseSemesterAccessRequest>,
    private val entityTemplate: R2dbcEntityTemplate
) {
    fun hasCourseAccess(semesterId: Long, appUser: AppUser, requestedRoles: List<Long>): Mono<Boolean> {
        val query = query(where("app_user_id").isEqual(appUser.id!!)
            .and("course_semester_role_id").isIn(requestedRoles))

        return entityTemplate.count(query, AppUserCourseSemesterRole::class.java)
            .map { it == requestedRoles.size.toLong() }
    }

    fun hasCourseAccess(semesterId: Long, appUser: AppUser, annotation: PreCourseSemesterAuthorize): Mono<Boolean> {
        val requestedRoles = annotation.value.map { it.id }

        return hasCourseAccess(semesterId, appUser, requestedRoles)
    }

    fun createCourseAccessRequest(appUser: AppUser, semesterId: Long): Mono<CourseSemesterAccessRequestQL> {
        return courseSemesterRepository.findById(semesterId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { checkOngoingCourseSemester(it) }
            .flatMap { hasCourseAccess(it.id!!, appUser, listOf(CourseSemesterRole.Value.ACCESS.id)) }
            .flatMap { if(it) Mono.error(AppUserCanAlreadyAccessSemesterException(appUser, semesterId)) else Mono.just(semesterId) }
            .then (storeCourseAccessRequestToCache(appUser, semesterId))
    }

    fun storeCourseAccessRequestToCache(
        appUser: AppUser,
        semesterId: Long
    ): Mono<CourseSemesterAccessRequestQL> {
        val accessRequest = CourseSemesterAccessRequest(appUser, semesterId)

        return cacheFactory.reactiveConnection
            .serverCommands()
            .flushAll()
            .then(
                courseAccessOps.opsForValue()
                    .set(accessRequest.toCacheKey(), accessRequest, Duration.ofSeconds(EXPIRATION))
            )
            .map { accessRequest.toQL() }
    }

    private fun checkOngoingCourseSemester(semester: CourseSemester): Mono<CourseSemester> {
        val now = LocalDateTime.now()

        if (semester.isBeforeStart(now)) {
            return Mono.error(CourseSemesterNotStartedYetException(semester, semester.dateStart!!, now))
        }
        if (semester.isAfterEnd(now)) {
            return Mono.error(CourseSemesterAlreadyEndedException(semester, semester.dateEnd!!, now))
        }

        return Mono.just(semester)
    }

    private fun hasGlobalPermission(courseAccess: CourseAuthorizeRequest) =
        CourseServiceV1.VIEW_PERMISSIONS.intersect(courseAccess.authorities.map { it.name }.toSet()).isNotEmpty()
}
