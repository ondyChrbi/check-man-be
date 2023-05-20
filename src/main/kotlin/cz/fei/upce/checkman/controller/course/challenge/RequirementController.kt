package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.dto.graphql.output.challenge.ChallengeQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL
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
    fun requirements(
        @ChallengeId @Argument challengeId: Long,
        @Argument pageSize: Int? = CheckManApplication.DEFAULT_PAGE_SIZE,
        @Argument page: Int? = CheckManApplication.DEFAULT_PAGE,
        authentication: Authentication
    ): Flux<RequirementQL> {
        return requirementService.findAllByChallengeIdAsQL(challengeId, pageSize, page)
    }

    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS])
    fun requirement(@Argument id: Long, authentication: Authentication): Mono<RequirementQL> {
        return requirementService.findById(id)
            .map { it.toQL() }
    }

    @SchemaMapping(typeName = "Challenge", field = "requirements")
    fun requirementsChallenge(challengeQL: ChallengeQL): Flux<RequirementQL> {
        return requirementService.findAllByChallengeIdAsQL(challengeQL.id!!)
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun createRequirement(@ChallengeId @Argument challengeId: Long, @Argument input: cz.fei.upce.checkman.dto.graphql.input.course.RequirementInputQL, authentication: Authentication) =
        requirementService.addAsQL(challengeId, input)

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun editRequirement(@RequirementId @Argument id: Long, @Argument input: cz.fei.upce.checkman.dto.graphql.input.course.RequirementInputQL, authentication: Authentication) =
        requirementService.editAsQL(id, input)

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun deleteRequirement(@RequirementId @Argument id: Long, authentication: Authentication): Mono<RequirementQL> {
        return requirementService.removeAsQL(id)
    }

    @SchemaMapping(typeName = "ReviewedRequirement")
    fun requirement(reviewedRequirement: ReviewedRequirementQL): Mono<RequirementQL> {
        return requirementService.findByReviewedRequirementIdAsQL(reviewedRequirement.id!!)
    }

}