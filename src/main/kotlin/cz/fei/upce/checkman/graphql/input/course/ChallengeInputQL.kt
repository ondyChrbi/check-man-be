package cz.fei.upce.checkman.graphql.input.course

import cz.fei.upce.checkman.domain.challenge.Challenge
import cz.fei.upce.checkman.domain.challenge.ChallengeKind
import cz.fei.upce.checkman.domain.user.AppUser
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class ChallengeInputQL (
    @field:NotEmpty(message = "{course.challenge.name.not-empty}")
    var name: String = "",
    @field:NotEmpty(message = "{course.challenge.description.not-empty}")
    var description: String = "",
    var deadlineDate: String? = null,
    var startDate: String? = null,
    @field: NotNull(message = "{course.challenge.challenge-kind.not-null}")
    var challengeKind: String
) {
    fun toEntity(semesterId: Long, appUser: AppUser): Challenge {
        return Challenge(
            name = name,
            description = description,
            deadlineDate = if(deadlineDate != null) LocalDateTime.parse(deadlineDate, DateTimeFormatter.ISO_DATE_TIME) else null,
            startDate = if(startDate != null) LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME) else null,
            courseSemesterId = semesterId,
            challengeKindId = ChallengeKind.Value.valueOf(challengeKind).id,
            authorId = appUser.id!!
        )
    }

    fun toEntity(semesterId: Long, challengeId: Long, appUser: AppUser): Challenge {
        val challenge = toEntity(semesterId, appUser)
        challenge.id = challengeId

        return challenge
    }
}
