package cz.fei.upce.checkman.graphql.output.challenge.requirement

data class ReviewedRequirementQL(
    var id: Long? = -1L,
    var points: Short? = 0,
    var description: String? = "",
    var requirement: RequirementQL? = null
)
