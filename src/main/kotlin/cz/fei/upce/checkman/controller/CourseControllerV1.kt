package cz.fei.upce.checkman.controller

import cz.fei.upce.checkman.dto.course.CourseDtoV1
import cz.fei.upce.checkman.service.course.CourseServiceV1
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/v1/course")
class CourseControllerV1(private var courseService: CourseServiceV1) {
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_COURSE_MANAGE')")
    fun add(@Valid @RequestBody courseDto: CourseDtoV1) = courseService.add(courseDto)

    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('ROLE_COURSE_MANAGE')")
    fun update(@Valid @RequestBody courseDto: CourseDtoV1, @PathVariable courseId: Long) =
        courseService.update(courseId, courseDto)

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ROLE_COURSE_MANAGE')")
    fun remove(@PathVariable courseId: Long) =
        courseService.delete(courseId).flatMap { Mono.just(ResponseEntity.noContent()) }
}