package cz.fei.upce.checkman.domain.challenge

import cz.fei.upce.checkman.graphql.output.challenge.PermittedAppUserChallengeQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.ZoneOffset

@Table("permitted_app_user_challenge")
data class PermittedAppUserChallenge(
    @Id var id: Long? = null,
    var accessTo: LocalDateTime = LocalDateTime.now(),
    var appUserId: Long = -1,
    var challengeId: Long = -1
) {
    fun toQL(): PermittedAppUserChallengeQL {
        return PermittedAppUserChallengeQL(id, accessTo.atOffset(ZoneOffset.UTC))
    }
}
