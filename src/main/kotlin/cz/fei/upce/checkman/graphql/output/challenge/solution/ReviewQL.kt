package cz.fei.upce.checkman.graphql.output.challenge.solution

import cz.fei.upce.checkman.graphql.output.challenge.requirement.ReviewedRequirementQL

data class ReviewQL (
    var id: Long,
    var description: String? = "",
    var requirements: List<ReviewedRequirementQL> = listOf(),
)