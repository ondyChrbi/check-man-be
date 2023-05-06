package cz.fei.upce.checkman.domain.review

import cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("requirement_review")
data class RequirementReview(
    @Id var id: Long? = -1L,
    var point: Short? = 0,
    var description: String? = "",
    var requirementId: Long? = -1L,
    var reviewId: Long? = -1L,
) {
    fun toReviewedRequirementQL(requirement: cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL): cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL {
        return cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL(
            id,
            point,
            description,
            requirement
        )
    }

    fun toQL(): cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL {
        return cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL(
            id,
            point,
            description
        )
    }
}
