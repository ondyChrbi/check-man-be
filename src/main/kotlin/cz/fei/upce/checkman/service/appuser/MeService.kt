package cz.fei.upce.checkman.service.appuser

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.course.CourseSemesterRole.Value.Companion.IDS_MAP
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.graphql.output.course.CourseQL
import cz.fei.upce.checkman.service.course.CourseService
import cz.fei.upce.checkman.service.course.security.CourseAuthorizationService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class MeService(
    private val courseService: CourseService,
    private val authorizationServiceV1: CourseAuthorizationService
    ) {
    fun myCoursesAsQL(appUser: AppUser,
                      pageSize: Int? = CheckManApplication.DEFAULT_PAGE_SIZE,
                      page: Int? = CheckManApplication.DEFAULT_PAGE
    ): Flux<CourseQL> {
        return courseService.findAllRelatedToAsQL(appUser, pageSize, page)
    }

    fun availableCoursesAsQL(
        appUser: AppUser,
        pageSize: Int? = CheckManApplication.DEFAULT_PAGE_SIZE,
        page: Int? = CheckManApplication.DEFAULT_PAGE
    ): Flux<CourseQL> {
        return courseService.findAvailableToAsQL(appUser)
    }

    fun courseRolesAsQL(semesterId: Long, appUser: AppUser): Flux<String> {
        return authorizationServiceV1.findAllCourseSemesterRoles(appUser, semesterId)
            .map { IDS_MAP[it.courseSemesterRoleId].toString() }
    }
}
