package cz.fei.upce.checkman.dto.graphql.output.challenge.solution

data class FeedbackQL(
    var id: Long? = -1,
    var description: String? = "",
    var type: String = ""
)
