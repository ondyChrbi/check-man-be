package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.graphql.input.course.RequirementInputQL
import cz.fei.upce.checkman.graphql.output.challenge.ChallengeQL
import cz.fei.upce.checkman.graphql.output.challenge.requirement.RequirementQL
import cz.fei.upce.checkman.graphql.output.challenge.requirement.ReviewedRequirementQL
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
class RequirementQLController(private val requirementService: RequirementService) {
    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS])
    fun requirements(@ChallengeId @Argument challengeId: Long, authentication: Authentication): Flux<RequirementQL> {
        return requirementService.findAllByChallengeIdAsQL(challengeId)
    }

    @SchemaMapping(typeName = "Challenge")
    fun requirements(challengeQL: ChallengeQL): Flux<RequirementQL> {
        return requirementService.findAllByChallengeIdAsQL(challengeQL.id!!)
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun createRequirement(@ChallengeId @Argument challengeId: Long, @Argument input: RequirementInputQL, authentication: Authentication) =
        requirementService.addAsQL(challengeId, input)

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun editRequirement(@RequirementId @Argument requirementId: Long, @Argument input: RequirementInputQL, authentication: Authentication) =
        requirementService.editAsQL(requirementId, input)

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun removeRequirement(@RequirementId @Argument requirementId: Long, authentication: Authentication): Mono<RequirementQL> {
        return requirementService.removeAsQL(requirementId)
    }

    @SchemaMapping(typeName = "ReviewedRequirement")
    fun requirement(reviewedRequirement: ReviewedRequirementQL): Mono<RequirementQL> {
        return requirementService.findByReviewedRequirementIdAsQL(reviewedRequirement.id!!)
    }

}