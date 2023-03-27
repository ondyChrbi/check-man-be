package cz.fei.upce.checkman.graphql.output.challenge.solution.statistic

data class FeedbackStatisticsQL(
    val semesterId: Long,
    val challengeId: Long,
    val description: String,
    val feedbackTypeId: Long,
    val feedbackName: String,
    val count: Long
)
