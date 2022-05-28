package cz.fei.upce.checkman.dto.course.challenge.requirement

import cz.fei.upce.checkman.domain.challenge.Challenge
import cz.fei.upce.checkman.domain.review.Requirement
import cz.fei.upce.checkman.dto.ResponseDto

data class RequirementResponseDtoV1 (
    var id: Long? = null,
    var name: String = "",
    var description: String = "",
    var minPoint: Short = 0,
    var maxPoint: Short = 0,
    var challenge: Challenge? = null
): ResponseDto<Requirement, RequirementResponseDtoV1>() {
    override fun withId(id: Long?): RequirementResponseDtoV1 {
        this.id = id

        return this
    }

    override fun toEntity() = Requirement(
        name = name,
        description = description,
        minPoint = minPoint,
        maxPoint = maxPoint
    )

    override fun toEntity(entity: Requirement): Requirement {
        entity.name = name
        entity.description = description
        entity.minPoint = minPoint
        entity.maxPoint = maxPoint

        return entity
    }

    fun toEntity(challengeId: Long) = Requirement(
        name = name,
        description = description,
        minPoint = minPoint,
        maxPoint = maxPoint,
        challengeId = challengeId
    )

    companion object {
        fun fromEntity(requirement: Requirement) = RequirementResponseDtoV1(
            requirement.id,
            requirement.name,
            requirement.description,
            requirement.minPoint,
            requirement.maxPoint
        )
    }
}
