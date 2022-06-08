package cz.fei.upce.checkman.service.appuser

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.course.CourseResponseDtoV1
import cz.fei.upce.checkman.service.course.CourseServiceV1
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class MeServiceV1(private val courseServiceV1: CourseServiceV1) {
    fun courses(appUser: AppUser): Flux<CourseResponseDtoV1> {
        return courseServiceV1.findAllRelatedTo(appUser)
    }

    fun availableCourses(appUser: AppUser): Flux<CourseResponseDtoV1> {
        return courseServiceV1.findAvailableTo(appUser)
    }
}