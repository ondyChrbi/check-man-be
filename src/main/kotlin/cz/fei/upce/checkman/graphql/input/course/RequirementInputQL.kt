package cz.fei.upce.checkman.graphql.input.course

import cz.fei.upce.checkman.domain.review.Requirement


data class RequirementInputQL (
    var name: String = "",
    var description: String = "",
    var minPoint: Short = 0,
    var maxPoint: Short = 0,
) {
    fun toEntity(challengeId: Long): Requirement {
        return Requirement(
            name = name,
            description = description,
            minPoint = minPoint,
            maxPoint = maxPoint,
            challengeId = challengeId
        )
    }

    fun toEntity(id: Long, challengeId: Long): Requirement {
        val requirement = toEntity(challengeId)
        requirement.id = id

        return requirement
    }
}
