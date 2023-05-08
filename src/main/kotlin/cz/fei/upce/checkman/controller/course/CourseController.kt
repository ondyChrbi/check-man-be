package cz.fei.upce.checkman.controller.course

import cz.fei.upce.checkman.domain.user.GlobalRole
import cz.fei.upce.checkman.service.course.CourseService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller

@Controller
class CourseController(
    private val courseService: CourseService
) {
    @QueryMapping
    fun courses() = courseService.findAllAsQL()

    @MutationMapping
    @PreAuthorize("hasRole('${GlobalRole.ROLE_COURSE_MANAGE}')")
    fun createCourse(@Argument input: cz.fei.upce.checkman.dto.graphql.input.course.CourseInputQL) = courseService.add(input)
}