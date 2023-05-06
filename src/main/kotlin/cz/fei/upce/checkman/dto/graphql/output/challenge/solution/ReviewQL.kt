package cz.fei.upce.checkman.dto.graphql.output.challenge.solution

import cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL

data class ReviewQL (
    var id: Long,
    var description: String? = "",
    var requirements: List<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL> = listOf(),
    var feedbacks: List<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL> = listOf(),
    var active: Boolean = true,
    var published: Boolean = false,
)