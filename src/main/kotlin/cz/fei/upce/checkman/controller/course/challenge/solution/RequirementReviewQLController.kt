package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.graphql.input.course.challenge.solution.ReviewPointsInputQL
import cz.fei.upce.checkman.graphql.output.challenge.requirement.ReviewedRequirementQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.ReviewQL
import cz.fei.upce.checkman.service.course.challenge.requirement.RequirementService
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.upce.fei.checkman.domain.course.security.annotation.RequirementId
import cz.upce.fei.checkman.domain.course.security.annotation.ReviewId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
@Validated
class RequirementReviewQLController(
    private val requirementService: RequirementService
) {
    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun editReviewPoints(@ReviewId @Argument reviewId: Long, @RequirementId @Argument requirementId: Long, @Argument reviewPointsInput : ReviewPointsInputQL, authentication: Authentication): Mono<Boolean> {
        return requirementService.editReviewPoints(reviewId, requirementId, reviewPointsInput)
    }

    @SchemaMapping(typeName = "Review")
    fun requirements(reviewQL: ReviewQL): Flux<ReviewedRequirementQL> {
        return requirementService.findAllRequirementReviewsByReviewIdAsQL(reviewQL.id)
    }
}