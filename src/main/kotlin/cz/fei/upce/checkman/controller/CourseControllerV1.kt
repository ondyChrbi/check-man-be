package cz.fei.upce.checkman.controller

import cz.fei.upce.checkman.dto.course.CourseDtoV1
import cz.fei.upce.checkman.service.course.CourseServiceV1
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/course")
class CourseControllerV1(private var courseService: CourseServiceV1) {
    @PostMapping("")
    fun add(@Valid @RequestBody courseDto: CourseDtoV1) = courseService.add(courseDto)
}