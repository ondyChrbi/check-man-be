package cz.fei.upce.checkman.service.course.security

import cz.fei.upce.checkman.domain.course.AppUserCourseSemesterRole
import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.domain.course.CourseSemesterAccessRequest
import cz.fei.upce.checkman.domain.course.CourseSemesterAccessRequest.Companion.EXPIRATION
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.graphql.output.course.CourseSemesterAccessRequestQL
import cz.fei.upce.checkman.service.ResourceNotFoundException
import cz.fei.upce.checkman.service.course.SemesterServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.fei.upce.checkman.service.course.security.exception.AppUserAlreadyRequestedAccessSemesterException
import cz.fei.upce.checkman.service.course.security.exception.AppUserCanAlreadyAccessSemesterException
import cz.fei.upce.checkman.service.course.security.exception.CourseSemesterAlreadyEndedException
import cz.fei.upce.checkman.service.course.security.exception.CourseSemesterNotStartedYetException
import cz.fei.upce.checkman.service.role.CourseSemesterRoleServiceV1
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.relational.core.query.Query.query
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.isEqual
import org.springframework.data.relational.core.query.isIn
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime

@Service
class CourseAuthorizationServiceV1(
    private val semesterService: SemesterServiceV1,
    private val courseSemesterRoleService: CourseSemesterRoleServiceV1,
    private val cacheFactory: ReactiveRedisConnectionFactory,
    private val courseAccessOps: ReactiveRedisOperations<String, CourseSemesterAccessRequest>,
    private val entityTemplate: R2dbcEntityTemplate
) {
    fun courseSemesterRoles(appUser: AppUser, semesterId: Long): Flux<AppUserCourseSemesterRole> {
        val query = query(
            where("app_user_id").isEqual(appUser.id!!)
                .and("course_semester_id").isEqual(semesterId)
        )

        return entityTemplate.select(AppUserCourseSemesterRole::class.java)
            .matching(query)
            .all()
    }

    fun findAllCoursesWhereUserHasRoles(courseId: Long, appUser: AppUser, requestedRoles: List<Long>): Flux<CourseSemester> {
        return semesterService.findAllByUserHasRolesInCourse(courseId, appUser.id!!, requestedRoles)
    }

    fun hasCourseAccess(semesterId: Long, appUser: AppUser, requestedRoles: List<Long>): Mono<Boolean> {
        val query = query(
            where("app_user_id").isEqual(appUser.id!!)
                .and("course_semester_id").isEqual(semesterId)
                .and("course_semester_role_id").isIn(requestedRoles)
        )

        return entityTemplate.count(query, AppUserCourseSemesterRole::class.java)
            .map { it == requestedRoles.size.toLong() }
    }

    fun hasCourseAccess(semesterId: Long, appUser: AppUser, annotation: PreCourseSemesterAuthorize): Mono<Boolean> {
        val requestedRoles = annotation.value.map { it.id }

        return hasCourseAccess(semesterId, appUser, requestedRoles)
    }

    fun createCourseAccessRequest(appUser: AppUser, semesterId: Long): Mono<CourseSemesterAccessRequestQL> {
        return checkAlreadyRequested(semesterId, appUser)
            .flatMap { semesterService.findById(semesterId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { checkOngoingCourseSemester(it) }
            .flatMap { hasCourseAccess(it.id!!, appUser, listOf(CourseSemesterRole.Value.ACCESS.id)) }
            .flatMap {
                if (it) Mono.error(AppUserCanAlreadyAccessSemesterException(appUser, semesterId)) else Mono.just(
                    semesterId
                )
            }
            .then(storeCourseAccessRequestToCache(appUser, semesterId))
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

    fun findAllCourseAccessRequestsBySemester(semesterId: Long): Flux<CourseSemesterAccessRequestQL> {
        return courseAccessOps.keys(CourseSemesterAccessRequest.cacheKeyPatternSemester(semesterId))
            .flatMap { courseAccessOps.opsForValue().get(it) }
            .map { it.toQL() }
    }

    fun findAllCourseAccessRequestsByAppUser(appUserId: Long): Flux<CourseSemesterAccessRequestQL> {
        return courseAccessOps.keys(CourseSemesterAccessRequest.cacheKeyPatternAppUser(appUserId))
            .flatMap { courseAccessOps.opsForValue().get(it) }
            .map { it.toQL() }
    }

    fun findAllCourseAccessRequestsBySemester(appUser: AppUserQL): Flux<CourseSemesterAccessRequestQL> {
        return findAllCourseAccessRequestsByAppUser(appUser.id!!)
    }

    fun findAllCourseAccessRequests(appUserId: Long, semesterId: Long): Mono<CourseSemesterAccessRequestQL> {
        return courseAccessOps.opsForValue().get(CourseSemesterAccessRequest.cacheKeyPattern(semesterId, appUserId))
            .map { it.toQL() }
    }

    fun approveCourseSemesterRequest(id: Long, roles: List<CourseSemesterRole.Value> = listOf()): Mono<Boolean> {
        return courseAccessOps.keys(CourseSemesterAccessRequest.cacheKeyPatternId(id))
            .flatMap { courseAccessOps.opsForValue().get(it) }
            .next()
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { addAppUserToCourseSemester(it, roles) }
    }

    private fun addAppUserToCourseSemester(
        accessRequest: CourseSemesterAccessRequest,
        roles: List<CourseSemesterRole.Value> = listOf()
    ): Mono<Boolean> {
        val appUserId = accessRequest.appUser.id!!
        val semesterId = accessRequest.semesterId

        return semesterService.checkExistById(semesterId)
            .flatMapMany { Flux.fromIterable(roles.map { it.id }) }
            .flatMap { roleId -> courseSemesterRoleService.addRole(appUserId, semesterId, roleId) }
            .next()
            .map { true }
    }

    private fun checkAlreadyRequested(semesterId: Long, appUser: AppUser): Mono<Boolean> {
        return isAlreadyRequested(semesterId, appUser)
            .flatMap {
                if (it)
                    Mono.error(AppUserAlreadyRequestedAccessSemesterException(appUser, semesterId))
                else
                    Mono.just(true)
            }
    }

    private fun isAlreadyRequested(semesterId: Long, appUser: AppUser): Mono<Boolean> {
        return courseAccessOps.keys(CourseSemesterAccessRequest.cacheKeyPattern(semesterId, appUser))
            .collectList()
            .map { it.size > 0 }
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
}
