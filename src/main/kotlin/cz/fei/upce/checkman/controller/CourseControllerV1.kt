package cz.fei.upce.checkman.controller

import cz.fei.upce.checkman.doc.CreateCourseEndpointV1
import cz.fei.upce.checkman.doc.DeleteCourseEndpointV1
import cz.fei.upce.checkman.doc.UpdateCourseEndpointV1
import cz.fei.upce.checkman.dto.course.CourseDtoV1
import cz.fei.upce.checkman.service.course.CourseServiceV1
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/v1/course")
@Tag(name = "Course V1", description = "Course API (V1)")
class CourseControllerV1(private var courseService: CourseServiceV1) {
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_COURSE_MANAGE')")
    @CreateCourseEndpointV1
    fun add(@Valid @RequestBody courseDto: CourseDtoV1) = courseService.add(courseDto)

    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('ROLE_COURSE_MANAGE')")
    @UpdateCourseEndpointV1
    fun update(@Valid @RequestBody courseDto: CourseDtoV1, @PathVariable courseId: Long) =
        courseService.update(courseId, courseDto)

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ROLE_COURSE_MANAGE')")
    @DeleteCourseEndpointV1
    fun remove(@PathVariable courseId: Long) =
        courseService.delete(courseId).flatMap { Mono.just(ResponseEntity.noContent()) }
}