package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.doc.course.CreateCourseEndpointV1
import cz.fei.upce.checkman.doc.course.challenge.DeleteRequirementEndpointV1
import cz.fei.upce.checkman.doc.course.challenge.FindRequirementByIdEndpointV1
import cz.fei.upce.checkman.doc.course.challenge.SearchRequirementByIdEndpointV1
import cz.fei.upce.checkman.doc.course.challenge.UpdateRequirementEndpointV1
import cz.fei.upce.checkman.domain.user.GlobalRole
import cz.fei.upce.checkman.dto.course.challenge.requirement.RequirementRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.requirement.RequirementResponseDtoV1
import cz.fei.upce.checkman.service.course.challenge.ChallengeLocation
import cz.fei.upce.checkman.service.course.challenge.requirement.RequirementServiceV1
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/v1/course/{courseId}/semester/{semesterId}/challenge/{challengeId}/requirement")
@Tag(name = "Requirement V1", description = "Requirement API (V1)")
class RequirementControllerV1(private val requirementService: RequirementServiceV1) {
    @GetMapping("")
    @PreAuthorize(
        """hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', 
            '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_VIEW}')"""
    )
    @SearchRequirementByIdEndpointV1
    fun search(
        @RequestParam(required = false, defaultValue = "") search: String?, @PathVariable courseId: Long,
        @PathVariable semesterId: Long, @PathVariable challengeId: Long
    ): Mono<ResponseEntity<CollectionModel<RequirementResponseDtoV1>>> {
        val location = ChallengeLocation(courseId, semesterId, challengeId)

        return requirementService.search(location, search)
            .flatMap { assignSelRef(location, it) }
            .collectList()
            .flatMap { assignSelfRef(location, it) }
            .map { ResponseEntity.ok(it) }
    }

    @GetMapping("/{requirementId}")
    @PreAuthorize(
        """hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', 
            '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_VIEW}')"""
    )
    @FindRequirementByIdEndpointV1
    fun find(
        @PathVariable courseId: Long, @PathVariable semesterId: Long, @PathVariable challengeId: Long,
        @PathVariable requirementId: Long
    ): Mono<ResponseEntity<RequirementResponseDtoV1>> {
        val location = ChallengeLocation(courseId, semesterId, challengeId)

        return requirementService.find(location, requirementId)
            .flatMap { assignSelRef(location, it) }
            .map { ResponseEntity.ok(it) }
    }

    @PostMapping("")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @CreateCourseEndpointV1
    fun add(
        @Valid @RequestBody requirementDto: RequirementRequestDtoV1, @PathVariable courseId: Long,
        @PathVariable semesterId: Long, @PathVariable challengeId: Long
    ): Mono<ResponseEntity<RequirementResponseDtoV1>> {
        val location = ChallengeLocation(courseId, semesterId, challengeId)

        return requirementService.add(location, requirementDto).map { ResponseEntity.ok(it) }
    }

    @PutMapping("/{requirementId}")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @UpdateRequirementEndpointV1
    fun update(
        @Valid @RequestBody requirementDto: RequirementRequestDtoV1, @PathVariable courseId: Long,
        @PathVariable semesterId: Long, @PathVariable challengeId: Long, @PathVariable requirementId: Long
    ): Mono<ResponseEntity<RequirementResponseDtoV1>> {
        val location = ChallengeLocation(courseId, semesterId, challengeId)

        return requirementService.update(location, challengeId, requirementDto).map { ResponseEntity.ok(it) }
    }

    @DeleteMapping("/{requirementId}")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @DeleteRequirementEndpointV1
    fun remove(
        @PathVariable courseId: Long, @PathVariable semesterId: Long, @PathVariable challengeId: Long,
        @PathVariable requirementId: Long
    ): Mono<ResponseEntity<String>> {
        val location = ChallengeLocation(courseId, semesterId, challengeId)

        return requirementService.delete(location, requirementId).then(Mono.just(ResponseEntity.noContent().build()))
    }

    private fun assignSelRef(
        location: ChallengeLocation,
        requirement: RequirementResponseDtoV1
    ): Mono<RequirementResponseDtoV1> {
        return linkTo(
            methodOn(this::class.java).find(
                location.courseId,
                location.semesterId,
                location.challengeId,
                requirement.id!!
            )
        )
            .withSelfRel()
            .toMono()
            .map { requirement.add(it) }
    }

    private fun assignSelfRef(
        location: ChallengeLocation,
        requirements: Collection<RequirementResponseDtoV1>
    ): Mono<CollectionModel<RequirementResponseDtoV1>> {
        return linkTo(
            methodOn(this::class.java).search(
                null,
                location.courseId,
                location.semesterId,
                location.challengeId
            )
        )
            .withSelfRel()
            .toMono()
            .map { CollectionModel.of(requirements, it) }
    }
}
