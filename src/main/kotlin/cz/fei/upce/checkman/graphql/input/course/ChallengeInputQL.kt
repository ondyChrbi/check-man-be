package cz.fei.upce.checkman.graphql.input.course

import cz.fei.upce.checkman.domain.challenge.Challenge
import cz.fei.upce.checkman.domain.challenge.ChallengeKind
import cz.fei.upce.checkman.domain.user.AppUser
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ChallengeInputQL (
    var name: String = "",
    var description: String = "",
    var deadlineDate: String? = null,
    var startDate: String? = null,
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
