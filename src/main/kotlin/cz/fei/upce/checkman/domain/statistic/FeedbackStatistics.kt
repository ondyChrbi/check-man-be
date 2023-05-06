package cz.fei.upce.checkman.domain.statistic

import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.statistic.FeedbackStatisticsQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "feedback_statistics")
data class FeedbackStatistics(
    @field:Id
    val challengeId: Long,

    @field:Column
    val semesterId: Long,

    @field:Column
    val description: String,

    @field:Column
    val feedbackTypeId: Long,

    @field:Column
    val feedbackName: String,

    @field:Column
    val count: Long
) {
    fun toQL(): cz.fei.upce.checkman.dto.graphql.output.challenge.solution.statistic.FeedbackStatisticsQL {
        return cz.fei.upce.checkman.dto.graphql.output.challenge.solution.statistic.FeedbackStatisticsQL(
            semesterId = semesterId,
            challengeId = challengeId,
            description = description,
            feedbackTypeId = feedbackTypeId,
            feedbackName = feedbackName,
            count = count
        )
    }
}
