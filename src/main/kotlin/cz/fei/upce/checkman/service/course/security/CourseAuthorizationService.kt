package cz.fei.upce.checkman.service.course.security

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.repository.course.AppUserCourseSemesterRoleRepository
import cz.fei.upce.checkman.service.course.AppUserCourseSemesterForbiddenException
import cz.fei.upce.checkman.service.course.CourseServiceV1
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

@Service
class CourseAuthorizationService(private val appUserCourseSemesterRoleRepository: AppUserCourseSemesterRoleRepository) {
    fun checkCourseSemesterAuthority(courseAccess: CourseAuthorizeRequest, courseSemesterRole: CourseSemesterRole.Value): Mono<Boolean> {
        return appUserCourseSemesterRoleRepository
            .existsByAppUserIdEqualsAndCourseSemesterIdEqualsAndCourseSemesterRoleIdEquals(
                courseAccess.appUser.id!!,
                courseAccess.semesterId,
                courseSemesterRole.id
            )
            .flatMap {
                if (!it) {
                    Mono.error(AppUserCourseSemesterForbiddenException(courseSemesterRole))
                } else {
                    Mono.just(it)
                }
            }
    }

    fun checkCourseAccess(courseAccess: CourseAuthorizeRequest, courseRoles: Array<CourseSemesterRole.Value>): Mono<Boolean> {
        return courseRoles.toFlux()
            .flatMap { checkCourseAccess(courseAccess, it) }
            .all { it == true }
    }

    fun checkCourseAccess(courseAccess: CourseAuthorizeRequest, courseRole: CourseSemesterRole.Value): Mono<Boolean> {
        if (hasGlobalPermission(courseAccess)) {
            return Mono.just(true)
        }

        return checkCourseSemesterAuthority(courseAccess, courseRole)
    }

    private fun hasGlobalPermission(courseAccess: CourseAuthorizeRequest) =
        CourseServiceV1.VIEW_PERMISSIONS.intersect(courseAccess.authorities.map { it.name }.toSet()).isNotEmpty()

    fun checkManageAccess(courseAccess: CourseAuthorizeRequest, courseRole: CourseSemesterRole.Value): Mono<Boolean> {
        if (CourseServiceV1.MANAGE_PERMISSIONS.intersect(courseAccess.authorities.map { it.name }.toSet()).isNotEmpty()) {
            return Mono.just(true)
        }

        return checkCourseSemesterAuthority(courseAccess, courseRole)
    }
}
