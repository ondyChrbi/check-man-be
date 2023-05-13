package cz.fei.upce.checkman.service.course.security

import cz.fei.upce.checkman.domain.course.AppUserCourseSemesterRole
import cz.fei.upce.checkman.domain.course.Semester
import cz.fei.upce.checkman.domain.course.CourseSemesterAccessRequest
import cz.fei.upce.checkman.domain.course.CourseSemesterAccessRequest.Companion.EXPIRATION
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.service.ResourceNotFoundException
import cz.fei.upce.checkman.service.course.SemesterService
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.fei.upce.checkman.service.course.security.exception.AppUserAlreadyRequestedAccessSemesterException
import cz.fei.upce.checkman.service.course.security.exception.AppUserCanAlreadyAccessSemesterException
import cz.fei.upce.checkman.service.course.security.exception.CourseSemesterAlreadyEndedException
import cz.fei.upce.checkman.service.course.security.exception.CourseSemesterNotStartedYetException
import cz.fei.upce.checkman.service.role.CourseSemesterRoleService
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
class CourseAuthorizationService(
    private val semesterService: SemesterService,
    private val courseSemesterRoleService: CourseSemesterRoleService,
    private val cacheFactory: ReactiveRedisConnectionFactory,
    private val courseAccessOps: ReactiveRedisOperations<String, CourseSemesterAccessRequest>,
    private val entityTemplate: R2dbcEntityTemplate
) {
    fun findAllCourseSemesterRoles(appUser: AppUser, semesterId: Long): Flux<AppUserCourseSemesterRole> {
        val query = query(
            where("app_user_id").isEqual(appUser.id!!)
                .and("course_semester_id").isEqual(semesterId)
        )

        return entityTemplate.select(AppUserCourseSemesterRole::class.java)
            .matching(query)
            .all()
    }

    fun findAllCoursesWhereUserHasRoles(courseId: Long, appUser: AppUser, requestedRoles: List<Long>): Flux<Semester> {
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

    fun createCourseAccessRequest(appUser: AppUser, semesterId: Long): Mono<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterAccessRequestQL> {
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
    ): Mono<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterAccessRequestQL> {
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

    fun findAllCourseAccessRequestsBySemester(semesterId: Long): Flux<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterAccessRequestQL> {
        return courseAccessOps.keys(CourseSemesterAccessRequest.cacheKeyPatternSemester(semesterId))
            .flatMap { courseAccessOps.opsForValue().get(it) }
            .map { it.toQL() }
    }

    fun findAllCourseAccessRequestsByAppUser(appUserId: Long): Flux<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterAccessRequestQL> {
        return courseAccessOps.keys(CourseSemesterAccessRequest.cacheKeyPatternAppUser(appUserId))
            .flatMap { courseAccessOps.opsForValue().get(it) }
            .map { it.toQL() }
    }

    fun findAllCourseAccessRequestsBySemester(appUser: cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL): Flux<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterAccessRequestQL> {
        return findAllCourseAccessRequestsByAppUser(appUser.id!!)
    }

    fun findAllCourseAccessRequests(appUserId: Long, semesterId: Long): Mono<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterAccessRequestQL> {
        return courseAccessOps.keys(CourseSemesterAccessRequest.cacheKeyPattern(semesterId, appUserId))
            .flatMap { courseAccessOps.opsForValue().get(it) }
            .next()
            .map { it.toQL() }
    }

    fun approveCourseSemesterRequest(id: String, roles: List<CourseSemesterRole.Value> = listOf()): Mono<Boolean> {
        return courseAccessOps.keys(CourseSemesterAccessRequest.cacheKeyPatternId(id))
            .flatMap { courseAccessOps.opsForValue().get(it) }
            .collectList()
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { addAppUserToCourseSemester(it.first(), roles) }
            .flatMap { courseAccessOps.opsForValue().delete(it.toCacheKey()) }
            .map { true }
    }

    private fun addAppUserToCourseSemester(
        accessRequest: CourseSemesterAccessRequest,
        roles: List<CourseSemesterRole.Value> = listOf()
    ): Mono<CourseSemesterAccessRequest> {
        val appUserId = accessRequest.appUser.id!!
        val semesterId = accessRequest.semesterId

        return semesterService.checkExistById(semesterId)
            .flatMapMany { Flux.fromIterable(roles.map { it.id }) }
            .flatMap { roleId -> courseSemesterRoleService.addRole(appUserId, semesterId, roleId) }
            .collectList()
            .map { accessRequest }
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

    private fun checkOngoingCourseSemester(semester: Semester): Mono<Semester> {
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
