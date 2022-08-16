package cz.fei.upce.checkman.graphql.output.challenge.requirement

data class RequirementQL (
    var id: Long? = null,
    var name: String = "",
    var description: String = "",
    var minPoint: Short = 0,
    var maxPoint: Short = 0,
)