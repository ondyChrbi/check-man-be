package cz.fei.upce.checkman.domain.review

import cz.fei.upce.checkman.graphql.output.challenge.requirement.ReviewedRequirementQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.ReviewQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("review")
data class Review(
    @Id var id: Long? = null,
    var description: String? = "",
    var solutionId: Long? = -1L,
    var appUserId: Long? = -1L,
    var feedbackId: Long? = -1L,
    var reviewTemplateId: Long? = -1L,
) {
    fun toQL(requirements: List<ReviewedRequirementQL> = listOf()): ReviewQL {
        return ReviewQL(id!!, description, requirements)
    }

    enum class Feedback(val id: Long) {
        EXTREMELY_POSITIVE(0),
        POSITIVE(1),
        NEUTRAL(2),
        NEGATIVE(3),
    }

    companion object {
        val IDS_MAP = mapOf(
            0L to Feedback.EXTREMELY_POSITIVE,
            1L to Feedback.POSITIVE,
            2L to Feedback.NEUTRAL,
            3L to Feedback.NEGATIVE,
        )
    }
}
