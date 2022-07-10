package cz.fei.upce.checkman.controller.appuser

import cz.fei.upce.checkman.controller.course.CourseControllerV1
import cz.fei.upce.checkman.doc.appuser.MeAvailableCoursesEndpointV1
import cz.fei.upce.checkman.doc.appuser.MeCoursesEndpointV1
import cz.fei.upce.checkman.doc.appuser.MeEndpointV1
import cz.fei.upce.checkman.domain.user.GlobalRole
import cz.fei.upce.checkman.dto.appuser.AppUserResponseDtoV1
import cz.fei.upce.checkman.dto.course.CourseResponseDtoV1
import cz.fei.upce.checkman.dto.course.CourseSemesterResponseDtoV1
import cz.fei.upce.checkman.service.appuser.AppUserServiceV1
import cz.fei.upce.checkman.service.appuser.MeServiceV1
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/me")
@Tag(name = "Me V1", description = "Current logged user (V1)")
class MeControllerV1(
    private val appUserService: AppUserServiceV1,
    private val meService: MeServiceV1,
    private val authenticationService: AuthenticationServiceV1
    ) {
    @GetMapping("")
    @MeEndpointV1
    fun me(authentication: Authentication?): Mono<ResponseEntity<AppUserResponseDtoV1>> {
        return appUserService.meAsDto(authenticationService.extractAuthenticateUser(authentication!!))
            .flatMap { assignSelfRef(it) }
            .map { ResponseEntity.ok(it) }
    }

    @GetMapping("/courses")
    @PreAuthorize("hasRole('${GlobalRole.ROLE_COURSE_ACCESS}')")
    @MeCoursesEndpointV1
    fun courses(authentication: Authentication?): Mono<ResponseEntity<CollectionModel<CourseResponseDtoV1>>> {
        return meService.courses(authenticationService.extractAuthenticateUser(authentication!!))
            .flatMap { assignSelfRef(it) }
            .collectList()
            .flatMap { assignSelfRefCourses(it) }
            .map { ResponseEntity.ok(it) }
    }

    @GetMapping("/courses/available")
    @PreAuthorize("hasRole('${GlobalRole.ROLE_COURSE_ACCESS}')")
    @MeAvailableCoursesEndpointV1
    fun availableCourses(authentication: Authentication?): Mono<ResponseEntity<CollectionModel<CourseResponseDtoV1>>> {
        return meService.availableCoursesAsDto(authenticationService.extractAuthenticateUser(authentication!!))
            .flatMap { assignSelfRef(it) }
            .collectList()
            .flatMap { assignSelfRefAvailable(it) }
            .map { ResponseEntity.ok(it) }
    }

    private fun assignSelfRef(appUserDto: AppUserResponseDtoV1): Mono<AppUserResponseDtoV1> =
        linkTo(methodOn(this::class.java).me(null))
            .withSelfRel()
            .toMono()
            .map { appUserDto.add(it) }

    private fun assignSelfRefCourses(courses: Collection<CourseResponseDtoV1>) =
        linkTo(methodOn(this::class.java).courses(null))
            .withSelfRel()
            .toMono()
            .map { CollectionModel.of(courses, it) }

    private fun assignSelfRefAvailable(courses: Collection<CourseResponseDtoV1>) =
        linkTo(methodOn(this::class.java).availableCourses(null))
            .withSelfRel()
            .toMono()
            .map { CollectionModel.of(courses, it) }

    private fun assignSelfRef(
        courseId: Long,
        courseSemester: CourseSemesterResponseDtoV1
    ): Mono<CourseSemesterResponseDtoV1> {
        return linkTo(methodOn(CourseControllerV1::class.java).findSemester(courseId, courseSemester.id!!))
            .withSelfRel()
            .toMono()
            .map { courseSemester.add(it) }
    }

    private fun assignSelfRef(course: CourseResponseDtoV1): Mono<CourseResponseDtoV1> {
        return linkTo(methodOn(CourseControllerV1::class.java).find(course.id!!))
            .withSelfRel()
            .toMono()
            .map { course.add(it) }
            .map { it.semesters }
            .flatMapMany { Flux.fromIterable(it) }
            .flatMap { assignSelfRef(course.id!!, it) }
            .collectList()
            .map { course.withSemesters(it) }
    }
}