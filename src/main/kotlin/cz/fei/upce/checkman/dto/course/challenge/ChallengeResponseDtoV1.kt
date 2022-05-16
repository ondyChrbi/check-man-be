package cz.fei.upce.checkman.dto.course.challenge

import cz.fei.upce.checkman.domain.challenge.Challenge
import cz.fei.upce.checkman.domain.challenge.ChallengeKind
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.ResponseDto
import java.time.LocalDateTime

data class ChallengeResponseDtoV1(
    var id: Long? = null,
    var name: String = "",
    var description: String = "",
    var deadlineDate: LocalDateTime? = null,
    var startDate: LocalDateTime? = null,
    var challengeKind: ChallengeKind.Value = ChallengeKind.Value.OPTIONAL
) : ResponseDto<Challenge, ChallengeResponseDtoV1>() {
    override fun withId(id: Long?): ChallengeResponseDtoV1 {
        this.id = id ?: 0
        return this
    }

    override fun toEntity() = Challenge(
        name = name,
        description = description,
        deadlineDate = deadlineDate,
        startDate = if (startDate == null) LocalDateTime.now() else startDate,
        challengeKindId = challengeKind.id
    )

    override fun toEntity(entity: Challenge): Challenge {
        entity.name = name
        entity.description = description
        entity.deadlineDate = deadlineDate
        entity.startDate = startDate
        entity.challengeKindId = challengeKind.id

        return entity
    }

    fun toEntity(author: AppUser, semesterId: Long): Challenge {
        val entity = toEntity()
        entity.authorId = author.id!!
        entity.courseSemesterId= semesterId

        return entity
    }

    companion object {
        fun fromEntity(challenge: Challenge) = ChallengeResponseDtoV1(
            challenge.id,
            challenge.name,
            challenge.description,
            challenge.deadlineDate,
            challenge.startDate,
            ChallengeKind.Value.getById(challenge.challengeKindId)
        )
    }
}