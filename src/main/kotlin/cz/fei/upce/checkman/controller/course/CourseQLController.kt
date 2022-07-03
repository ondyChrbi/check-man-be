package cz.fei.upce.checkman.controller.course

import cz.fei.upce.checkman.service.course.CourseServiceV1
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.stereotype.Controller
import org.springframework.graphql.data.method.annotation.QueryMapping

@Controller
class CourseQLController(private val courseServiceV1: CourseServiceV1) {
    @QueryMapping
    fun courses() = courseServiceV1.findAllAsQL()

    @QueryMapping
    fun course(@Argument id: Long) = courseServiceV1.findAsQL(id)
}