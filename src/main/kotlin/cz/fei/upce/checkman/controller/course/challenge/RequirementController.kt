package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.service.course.challenge.requirement.RequirementService
import cz.upce.fei.checkman.domain.course.security.annotation.ChallengeId
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.upce.fei.checkman.domain.course.security.annotation.RequirementId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class RequirementController(private val requirementService: RequirementService) {
    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS])
    fun requirements(@ChallengeId @Argument challengeId: Long, authentication: Authentication): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL> {
        return requirementService.findAllByChallengeIdAsQL(challengeId)
    }

    @SchemaMapping(typeName = "Challenge")
    fun requirements(challengeQL: cz.fei.upce.checkman.dto.graphql.output.challenge.ChallengeQL): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL> {
        return requirementService.findAllByChallengeIdAsQL(challengeQL.id!!)
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun createRequirement(@ChallengeId @Argument challengeId: Long, @Argument input: cz.fei.upce.checkman.dto.graphql.input.course.RequirementInputQL, authentication: Authentication) =
        requirementService.addAsQL(challengeId, input)

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun editRequirement(@RequirementId @Argument requirementId: Long, @Argument input: cz.fei.upce.checkman.dto.graphql.input.course.RequirementInputQL, authentication: Authentication) =
        requirementService.editAsQL(requirementId, input)

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun removeRequirement(@RequirementId @Argument requirementId: Long, authentication: Authentication): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL> {
        return requirementService.removeAsQL(requirementId)
    }

    @SchemaMapping(typeName = "ReviewedRequirement")
    fun requirement(reviewedRequirement: cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL> {
        return requirementService.findByReviewedRequirementIdAsQL(reviewedRequirement.id!!)
    }

}