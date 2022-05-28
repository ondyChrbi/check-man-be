package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.doc.course.CreateCourseEndpointV1
import cz.fei.upce.checkman.domain.user.GlobalRole
import cz.fei.upce.checkman.dto.course.challenge.requirement.RequirementRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.requirement.RequirementResponseDtoV1
import cz.fei.upce.checkman.service.course.challenge.ChallengeLocation
import cz.fei.upce.checkman.service.course.challenge.requirement.RequirementServiceV1
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/v1/course/{courseId}/semester/{semesterId}/challenge/{challengeId}/requirement")
@Tag(name = "Requirement V1", description = "Requirement API (V1)")
class RequirementControllerV1(private val requirementService: RequirementServiceV1) {
    @PostMapping("")
    @PreAuthorize("hasRole('${GlobalRole.ROLE_COURSE_MANAGE}')")
    @CreateCourseEndpointV1
    fun add(@Valid @RequestBody requirementDto: RequirementRequestDtoV1, @PathVariable courseId: Long,
            @PathVariable semesterId: Long, @PathVariable challengeId: Long
    ) : Mono<ResponseEntity<RequirementResponseDtoV1>> {
        val location = ChallengeLocation(courseId, semesterId, challengeId)

        return requirementService.add(location, requirementDto).map { ResponseEntity.ok(it) }
    }
}