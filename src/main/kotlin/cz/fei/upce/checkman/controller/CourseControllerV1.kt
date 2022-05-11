package cz.fei.upce.checkman.controller

import cz.fei.upce.checkman.doc.course.CreateCourseEndpointV1
import cz.fei.upce.checkman.doc.course.DeleteCourseEndpointV1
import cz.fei.upce.checkman.doc.course.UpdateCourseEndpointV1
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_COURSE_MANAGE
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_COURSE_SEMESTER_MANAGE
import cz.fei.upce.checkman.dto.course.CourseDtoV1
import cz.fei.upce.checkman.dto.course.CourseSemesterDtoV1
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
    @PreAuthorize("hasRole('$ROLE_COURSE_MANAGE')")
    @CreateCourseEndpointV1
    fun add(@Valid @RequestBody courseDto: CourseDtoV1) = courseService.add(courseDto)

    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('$ROLE_COURSE_MANAGE')")
    @UpdateCourseEndpointV1
    fun update(@Valid @RequestBody courseDto: CourseDtoV1, @PathVariable courseId: Long) =
        courseService.update(courseId, courseDto)

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('$ROLE_COURSE_MANAGE')")
    @DeleteCourseEndpointV1
    fun remove(@PathVariable courseId: Long) =
        courseService.delete(courseId).flatMap { Mono.just(ResponseEntity.noContent()) }

    @PostMapping("/{courseId}/semester")
    @PreAuthorize("hasAnyRole('$ROLE_COURSE_MANAGE', '$ROLE_COURSE_SEMESTER_MANAGE')")
    fun addSemester(@Valid @RequestBody courseSemesterDto: CourseSemesterDtoV1, @PathVariable courseId: Long) =
        courseService.addSemester(courseId, courseSemesterDto)

    @PutMapping("/{courseId}/semester/{semesterId}")
    @PreAuthorize("hasAnyRole('$ROLE_COURSE_MANAGE', '$ROLE_COURSE_SEMESTER_MANAGE')")
    fun updateSemester(
        @Valid @RequestBody courseSemesterDto: CourseSemesterDtoV1, @PathVariable courseId: Long,
        @PathVariable semesterId: Long) =
        courseService.updateSemester(courseId, semesterId, courseSemesterDto)

    @DeleteMapping("/{courseId}/semester/{semesterId}")
    @PreAuthorize("hasAnyRole('$ROLE_COURSE_MANAGE', '$ROLE_COURSE_SEMESTER_MANAGE')")
    fun deleteSemester(@PathVariable courseId: Long, @PathVariable semesterId: Long) = courseService.deleteSemester(courseId, semesterId)
}