package cz.fei.upce.checkman.domain.review

import cz.fei.upce.checkman.graphql.output.challenge.requirement.ReviewedRequirementQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.FeedbackQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.ReviewQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("review")
data class Review(
    @Id var id: Long? = null,
    var description: String? = "",
    var solutionId: Long? = -1L,
    var appUserId: Long? = -1L,
    var reviewTemplateId: Long? = null,
    var active: Boolean = true,
    var published: Boolean = false,
) {
    fun toQL(requirements: List<ReviewedRequirementQL> = listOf(), feedbacks: List<FeedbackQL> = listOf()): ReviewQL {
        return ReviewQL(id!!, description, requirements, feedbacks, active, published)
    }
}
