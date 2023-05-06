package cz.fei.upce.checkman.dto.graphql.output.challenge.requirement

data class ReviewedRequirementQL(
    var id: Long? = -1L,
    var points: Short? = 0,
    var description: String? = "",
    var requirement: cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL? = null
)
