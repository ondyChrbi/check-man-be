package cz.fei.upce.checkman.service.course.security

import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.domain.course.CourseSemesterAccessRequest
import cz.fei.upce.checkman.domain.course.CourseSemesterAccessRequest.Companion.EXPIRATION
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.graphql.output.course.CourseSemesterAccessRequestQL
import cz.fei.upce.checkman.repository.course.AppUserCourseSemesterRoleRepository
import cz.fei.upce.checkman.repository.course.CourseSemesterRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import cz.fei.upce.checkman.service.course.CourseServiceV1
import cz.fei.upce.checkman.service.course.security.exception.AppUserCanAlreadyAccessSemesterException
import cz.fei.upce.checkman.service.course.security.exception.CourseSemesterAlreadyEndedException
import cz.fei.upce.checkman.service.course.security.exception.CourseSemesterNotStartedYetException
import cz.fei.upce.checkman.service.role.GlobalRoleServiceV1
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration
import java.time.LocalDateTime

@Service
class CourseAuthorizationServiceV1(
    private val globalRoleService: GlobalRoleServiceV1,
    private val courseSemesterRepository: CourseSemesterRepository,
    private val appUserCourseSemesterRoleRepository: AppUserCourseSemesterRoleRepository,
    private val cacheFactory: ReactiveRedisConnectionFactory,
    private val courseAccessOps: ReactiveRedisOperations<String, CourseSemesterAccessRequest>
) {

    fun hasCourseAccess(
        courseAccess: CourseAuthorizeRequest,
        courseRoles: Array<CourseSemesterRole.Value>
    ): Mono<Boolean> {
        return courseRoles
            .toFlux()
            .flatMap { hasCourseAccess(courseAccess, it) }
            .all { it == true }
    }

    fun createCourseAccessRequest(appUser: AppUser, semesterId: Long): Mono<CourseSemesterAccessRequestQL> {
        return courseSemesterRepository.findById(semesterId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap(this::checkOngoingCourseSemester)
            .flatMap { checkCourseAccess(appUser, it) }
            .flatMap { createCourseAccessRequest(appUser, it) }
    }

    fun createCourseAccessRequest(
        appUser: AppUser,
        courseSemester: CourseSemester
    ): Mono<CourseSemesterAccessRequestQL> {
        val accessRequest = CourseSemesterAccessRequest(appUser, courseSemester)

        return cacheFactory.reactiveConnection
            .serverCommands()
            .flushAll()
            .then(
                courseAccessOps.opsForValue()
                    .set(accessRequest.toCacheKey(), accessRequest, Duration.ofSeconds(EXPIRATION))
            )
            .map { accessRequest.toQL() }
    }

    private fun checkCourseAccess(appUser: AppUser, courseSemester: CourseSemester): Mono<CourseSemester> {
        return hasCourseAccess(appUser, courseSemester)
            .flatMap {
                if (!it)
                    Mono.just(courseSemester)
                else
                    Mono.error(AppUserCanAlreadyAccessSemesterException(appUser, courseSemester))
            }
    }

    private fun hasCourseAccess(appUser: AppUser, courseSemester: CourseSemester): Mono<Boolean> {
        return globalRoleService.rolesByUser(appUser)
            .collectList()
            .map { CourseAuthorizeRequest(courseSemester.courseId!!, courseSemester.id!!, appUser, it.toSet()) }
            .flatMap { hasCourseAccess(it, arrayOf(CourseSemesterRole.Value.ACCESS)) }
    }

    private fun hasCourseAccess(
        courseAccess: CourseAuthorizeRequest,
        courseRole: CourseSemesterRole.Value
    ): Mono<Boolean> {
        if (hasGlobalPermission(courseAccess)) {
            return Mono.just(true)
        }

        return checkCourseSemesterAuthority(courseAccess, courseRole)
    }

    private fun checkCourseSemesterAuthority(
        courseAccess: CourseAuthorizeRequest,
        courseSemesterRole: CourseSemesterRole.Value
    ): Mono<Boolean> {
        return appUserCourseSemesterRoleRepository
            .existsByAppUserIdEqualsAndCourseSemesterIdEqualsAndCourseSemesterRoleIdEquals(
                courseAccess.appUser.id!!,
                courseAccess.semesterId,
                courseSemesterRole.id
            )
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
