package cz.fei.upce.checkman.controller

import cz.fei.upce.checkman.doc.course.*
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_COURSE_MANAGE
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_COURSE_SEMESTER_MANAGE
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_COURSE_SEMESTER_VIEW
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_COURSE_VIEW
import cz.fei.upce.checkman.dto.course.CourseDtoV1
import cz.fei.upce.checkman.dto.course.CourseSemesterDtoV1
import cz.fei.upce.checkman.service.course.CourseServiceV1
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/v1/course")
@Tag(name = "Course V1", description = "Course API (V1)")
class CourseControllerV1(private var courseService: CourseServiceV1) {
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('$ROLE_COURSE_VIEW')")
    @FindCourseByIdEndpointV1
    fun find(@PathVariable id: Long): Mono<ResponseEntity<CourseDtoV1>> {
        return courseService.find(id).flatMap { assignSelfRef(it) }.map { ResponseEntity.ok(it) }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('$ROLE_COURSE_MANAGE')")
    @CreateCourseEndpointV1
    fun add(@Valid @RequestBody courseDto: CourseDtoV1) = courseService.add(courseDto)

    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('$ROLE_COURSE_MANAGE')")
    @UpdateCourseEndpointV1
    fun update(@Valid @RequestBody courseDto: CourseDtoV1, @PathVariable courseId: Long) =
        courseService.update(courseId, courseDto).map { ResponseEntity.ok(it) }

    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('$ROLE_COURSE_MANAGE')")
    @DeleteCourseEndpointV1
    fun remove(@PathVariable courseId: Long) =
        courseService.delete(courseId).flatMap { Mono.just(ResponseEntity.noContent()) }

    @GetMapping("/{courseId}/semester/{semesterId}")
    @PreAuthorize("hasAnyRole('$ROLE_COURSE_VIEW', '$ROLE_COURSE_SEMESTER_VIEW')")
    @FindCourseSemesterByIdEndpointV1
    fun findSemester(
        @PathVariable courseId: Long,
        @PathVariable semesterId: Long
    ): Mono<ResponseEntity<CourseSemesterDtoV1>> {
        return courseService.findSemester(courseId, semesterId).map { ResponseEntity.ok(it) }
    }

    @PostMapping("/{courseId}/semester")
    @PreAuthorize("hasAnyRole('$ROLE_COURSE_MANAGE', '$ROLE_COURSE_SEMESTER_MANAGE')")
    @CreateCourseSemesterEndpointV1
    fun addSemester(@Valid @RequestBody courseSemesterDto: CourseSemesterDtoV1, @PathVariable courseId: Long) =
        courseService.addSemester(courseId, courseSemesterDto).map { ResponseEntity.ok(it) }

    @PutMapping("/{courseId}/semester/{semesterId}")
    @PreAuthorize("hasAnyRole('$ROLE_COURSE_MANAGE', '$ROLE_COURSE_SEMESTER_MANAGE')")
    @UpdateCourseSemesterEndpointV1
    fun updateSemester(
        @Valid @RequestBody courseSemesterDto: CourseSemesterDtoV1, @PathVariable courseId: Long,
        @PathVariable semesterId: Long
    ) = courseService.updateSemester(courseId, semesterId, courseSemesterDto).map { ResponseEntity.ok(it) }

    @DeleteMapping("/{courseId}/semester/{semesterId}")
    @PreAuthorize("hasAnyRole('$ROLE_COURSE_MANAGE', '$ROLE_COURSE_SEMESTER_MANAGE')")
    @DeleteCourseSemesterEndpointV1
    fun removeSemester(@PathVariable courseId: Long, @PathVariable semesterId: Long) =
        courseService.deleteSemester(courseId, semesterId).flatMap { Mono.just(ResponseEntity.noContent()) }

    private fun assignSelfRef(course: CourseDtoV1): Mono<CourseDtoV1> {
        return linkTo(methodOn(this::class.java).find(course.id!!))
            .withSelfRel()
            .toMono()
            .map { course.add(it) }
            .map { it.semesters }
            .flatMapMany { Flux.fromIterable(it) }
            .flatMap { assignSelfRef(course, it) }
            .collectList()
            .map { course.withSemesters(it) }
    }

    private fun assignSelfRef(course: CourseDtoV1, courseSemester: CourseSemesterDtoV1): Mono<CourseSemesterDtoV1> {
        return linkTo(methodOn(this::class.java).findSemester(course.id!!, courseSemester.id!!))
            .withSelfRel()
            .toMono()
            .map { courseSemester.add(it) }
    }
}