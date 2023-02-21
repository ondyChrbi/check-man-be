package cz.fei.upce.checkman.graphql.input.course.challenge

import cz.fei.upce.checkman.domain.challenge.Challenge
import cz.fei.upce.checkman.domain.challenge.ChallengeKind
import cz.fei.upce.checkman.domain.user.AppUser
import org.springframework.format.annotation.DateTimeFormat
import java.time.OffsetDateTime
import javax.validation.constraints.*

data class ChallengeInputQL (
    @field:NotNull
    @field:NotEmpty
    @field:NotBlank
    @field:Size(max = 128)
    var name: String = "",
    @field:Size(max = 5000)
    var description: String = "",
    @field:DateTimeFormat
    var deadlineDate: OffsetDateTime? = null,
    @field:DateTimeFormat
    var startDate: OffsetDateTime? = null,
    var challengeKind: String
) {
    fun toEntity(semesterId: Long, appUser: AppUser): Challenge {
        return Challenge(
            name = name,
            description = description,
            deadlineDate = deadlineDate?.toLocalDateTime(),
            startDate = startDate?.toLocalDateTime(),
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
