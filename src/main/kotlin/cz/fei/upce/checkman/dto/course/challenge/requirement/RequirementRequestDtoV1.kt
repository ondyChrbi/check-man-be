package cz.fei.upce.checkman.dto.course.challenge.requirement

import cz.fei.upce.checkman.dto.RequestDto

data class RequirementRequestDtoV1 (
    var name: String = "",
    var description: String = "",
    var minPoint: Short = 0,
    var maxPoint: Short = 0,
): RequestDto<RequirementRequestDtoV1, RequirementResponseDtoV1> {
    override fun toResponseDto() = RequirementResponseDtoV1(
        name = name,
        description = description,
        minPoint = minPoint,
        maxPoint = maxPoint
    )

    override fun preventNullCollections() = this
}
