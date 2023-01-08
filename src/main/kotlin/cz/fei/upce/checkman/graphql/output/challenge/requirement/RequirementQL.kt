package cz.fei.upce.checkman.graphql.output.challenge.requirement

data class RequirementQL (
    var id: Long,
    var name: String = "",
    var description: String?,
    var minPoint: Int? = 0,
    var maxPoint: Int? = 0,
    var removed: Boolean = false
)