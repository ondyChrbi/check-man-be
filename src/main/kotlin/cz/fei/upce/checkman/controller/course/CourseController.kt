package cz.fei.upce.checkman.controller.course

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.user.GlobalRole
import cz.fei.upce.checkman.dto.graphql.output.course.CourseQL
import cz.fei.upce.checkman.service.course.CourseService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class CourseController(
    private val courseService: CourseService
) {
    @QueryMapping
    fun courses(
        @Argument pageSize: Int? = CheckManApplication.DEFAULT_LIMIT,
        @Argument page: Int? = CheckManApplication.DEFAULT_OFFSET,
    ): Flux<CourseQL> {
        return courseService.findAllAsQL(pageSize, page)
    }

    @MutationMapping
    @PreAuthorize("hasRole('${GlobalRole.ROLE_COURSE_MANAGE}')")
    fun createCourse(@Argument input: cz.fei.upce.checkman.dto.graphql.input.course.CourseInputQL) = courseService.add(input)

    @MutationMapping
    @PreAuthorize("hasRole('${GlobalRole.ROLE_COURSE_MANAGE}')")
    fun editCourse(@Argument id: Long, @Argument input: cz.fei.upce.checkman.dto.graphql.input.course.CourseInputQL): Mono<CourseQL> {
        return courseService.edit(id, input)
            .map { it.toQL() }
    }

    @MutationMapping
    @PreAuthorize("hasRole('${GlobalRole.ROLE_COURSE_MANAGE}')")
    fun deleteCourse(@Argument id: Long): Mono<Boolean> {
        return courseService.delete(id)
            .map { true }
    }

}