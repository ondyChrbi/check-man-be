package cz.fei.upce.checkman.graphql.output.challenge.requirement

data class RequirementQL (
    var id: Long? = null,
    var name: String = "",
    var description: String = "",
    var minPoint: Short = 0,
    var maxPoint: Short = 0,
    /**
     * Its filled only when user has course semester role SUBMIT_CHALLENGE_SOLUTION and submit
     * solution with review of teacher.
     * */
    var total: Short? = null
)