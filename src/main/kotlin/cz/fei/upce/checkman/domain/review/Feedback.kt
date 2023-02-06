package cz.fei.upce.checkman.domain.review

import cz.fei.upce.checkman.graphql.output.challenge.solution.FeedbackQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("feedback")
data class Feedback(
    @Id var id: Long? = null,
    var description: String? = "",
    var feedbackTypeId: Long = -1,
    var reviewId: Long = -1
) {
    fun toQL(): FeedbackQL {
        return FeedbackQL(id, description, getById(feedbackTypeId).toString())
    }

    enum class FeedbackType {
        EXTREMELY_POSITIVE,
        POSITIVE,
        NEUTRAL,
        NEGATIVE
    }

    companion object {
        val IDS_MAP = mapOf(
            0L to FeedbackType.EXTREMELY_POSITIVE,
            1L to FeedbackType.POSITIVE,
            2L to FeedbackType.NEUTRAL,
            3L to FeedbackType.NEGATIVE
        )

        fun getById(id: Long) = FeedbackType.values()[id.toInt()]
    }
}
