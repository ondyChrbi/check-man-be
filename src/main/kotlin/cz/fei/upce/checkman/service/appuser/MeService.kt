package cz.fei.upce.checkman.service.appuser

import cz.fei.upce.checkman.domain.course.CourseSemesterRole.Value.Companion.IDS_MAP
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.course.CourseResponseDtoV1
import cz.fei.upce.checkman.graphql.output.course.CourseQL
import cz.fei.upce.checkman.service.course.CourseService
import cz.fei.upce.checkman.service.course.security.CourseAuthorizationService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class MeService(
    private val courseService: CourseService,
    private val authorizationServiceV1: CourseAuthorizationService
    ) {
    fun myCoursesAsDto(appUser: AppUser): Flux<CourseResponseDtoV1> {
        return courseService.findAllRelatedToAsDto(appUser)
    }

    fun myCoursesAsQL(appUser: AppUser): Flux<CourseQL> {
        return courseService.findAllRelatedToAsQL(appUser)
    }

    fun availableCoursesAsDto(appUser: AppUser): Flux<CourseResponseDtoV1> {
        return courseService.findAvailableToAsDto(appUser)
    }

    fun availableCoursesAsQL(appUser: AppUser): Flux<CourseQL> {
        return courseService.findAvailableToAsQL(appUser)
    }

    fun courseRolesAsQL(semesterId: Long, appUser: AppUser): Flux<String> {
        return authorizationServiceV1.findAllCourseSemesterRoles(appUser, semesterId)
            .map { IDS_MAP[it.courseSemesterRoleId].toString() }
    }
}
