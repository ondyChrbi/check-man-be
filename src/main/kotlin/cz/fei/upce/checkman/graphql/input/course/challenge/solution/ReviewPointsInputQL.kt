package cz.fei.upce.checkman.graphql.input.course.challenge.solution

import cz.fei.upce.checkman.domain.review.RequirementReview
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Validated
data class ReviewPointsInputQL(
    @field:Min(0)
    @field:Max(100)
    var points: Short
) {
    fun toEntity(reviewId: Long, requirementId: Long): RequirementReview {
        return toEntity(null, reviewId, requirementId)
    }

    fun toEntity(id: Long?, reviewId: Long, requirementId: Long): RequirementReview {
        return RequirementReview(id = id, point = points, requirementId = requirementId, reviewId = reviewId)
    }
}
