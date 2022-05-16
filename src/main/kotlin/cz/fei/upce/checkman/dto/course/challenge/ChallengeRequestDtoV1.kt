package cz.fei.upce.checkman.dto.course.challenge

import cz.fei.upce.checkman.domain.challenge.ChallengeKind
import cz.fei.upce.checkman.dto.RequestDto
import java.time.LocalDateTime
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class ChallengeRequestDtoV1(
    @field:NotEmpty(message = "{course.challenge.name.not-empty}")
    var name: String = "",
    @field:NotEmpty(message = "{course.challenge.description.not-empty}")
    var description: String = "",
    var deadlineDate: LocalDateTime? = null,
    var startDate: LocalDateTime? = null,
    @field: NotNull(message = "{course.challenge.challenge-kind.not-null}")
    var challengeKind: ChallengeKind.Value
) : RequestDto<ChallengeRequestDtoV1, ChallengeResponseDtoV1> {
    override fun toResponseDto(): ChallengeResponseDtoV1 = ChallengeResponseDtoV1(
        name = name,
        description = description,
        deadlineDate = deadlineDate,
        startDate = startDate,
        challengeKind = challengeKind
    )

    override fun preventNullCollections() = this

}
