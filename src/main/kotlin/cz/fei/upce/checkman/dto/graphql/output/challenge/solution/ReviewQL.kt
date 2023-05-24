package cz.fei.upce.checkman.dto.graphql.output.challenge.solution

data class ReviewQL (
    var id: Long,
    var description: String? = "",
    var requirements: List<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL> = listOf(),
    var feedbacks: List<FeedbackQL> = listOf(),
    var active: Boolean = true,
    var published: Boolean = false,
)