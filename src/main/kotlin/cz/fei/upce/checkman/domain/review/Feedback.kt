package cz.fei.upce.checkman.domain.review

import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("feedback")
data class Feedback(
    @Id var id: Long? = null,
    var description: String? = "",
    var feedbackTypeId: Long = -1
) {
    fun toQL(): cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL {
        return cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL(
            id,
            description,
            getById(feedbackTypeId).toString()
        )
    }

    enum class FeedbackType(val id: Long) {
        EXTREMELY_POSITIVE(0L),
        POSITIVE(1L),
        NEUTRAL(2L),
        NEGATIVE(3L);
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
