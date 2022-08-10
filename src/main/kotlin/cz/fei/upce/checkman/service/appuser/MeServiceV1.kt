package cz.fei.upce.checkman.service.appuser

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.course.CourseResponseDtoV1
import cz.fei.upce.checkman.graphql.output.course.CourseQL
import cz.fei.upce.checkman.service.course.CourseServiceV1
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class MeServiceV1(private val courseServiceV1: CourseServiceV1) {
    fun myCoursesAsDto(appUser: AppUser): Flux<CourseResponseDtoV1> {
        return courseServiceV1.findAllRelatedToAsDto(appUser)
    }

    fun myCoursesAsQL(appUser: AppUser): Flux<CourseQL> {
        return courseServiceV1.findAllRelatedToAsQL(appUser)
    }

    fun availableCoursesAsDto(appUser: AppUser): Flux<CourseResponseDtoV1> {
        return courseServiceV1.findAvailableToAsDto(appUser)
    }

    fun availableCoursesAsQL(appUser: AppUser): Flux<CourseQL> {
        return courseServiceV1.findAvailableToAsQL(appUser)
    }
}
