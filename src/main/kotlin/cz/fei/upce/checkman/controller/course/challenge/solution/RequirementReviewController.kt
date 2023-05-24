package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.ReviewQL
import cz.fei.upce.checkman.service.course.challenge.requirement.RequirementService
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.upce.fei.checkman.domain.course.security.annotation.RequirementId
import cz.upce.fei.checkman.domain.course.security.annotation.ReviewId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
@Validated
class RequirementReviewController(
    private val requirementService: RequirementService
) {
    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun requirementReview(@ReviewId @Argument reviewId: Long, @RequirementId @Argument requirementId: Long, authentication: Authentication): Mono<ReviewedRequirementQL> {
        return requirementService.findByReviewIdAndRequirementId(reviewId, requirementId)
            .map { it.toQL() }
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun editReviewPoints(@ReviewId @Argument reviewId: Long, @RequirementId @Argument requirementId: Long, @Argument reviewPointsInput : cz.fei.upce.checkman.dto.graphql.input.course.challenge.solution.ReviewPointsInputQL, authentication: Authentication): Mono<Boolean> {
        return requirementService.editReviewPoints(reviewId, requirementId, reviewPointsInput)
    }

    @SchemaMapping(typeName = "Review")
    fun requirements(reviewQL: ReviewQL): Flux<RequirementQL> {
        return requirementService.findAllByReviewIdAsQL(reviewQL.id)
            .map { it.toQL() }
    }

    @SchemaMapping(typeName = "Review", field = "reviewRequirements")
    fun reviewRequirements(reviewQL: ReviewQL, authentication: Authentication): Flux<ReviewedRequirementQL> {
        return requirementService.findAllByReviewId(reviewQL.id)
            .map { it.toQL() }
    }
}