package cz.fei.upce.checkman.domain.challenge

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("permitted_app_user_challenge")
data class PermittedAppUserChallenge(
    @Id var id: Long? = null,
    var accessTo: LocalDateTime = LocalDateTime.now(),
    var appUserId: Long = -1,
    var challengeId: Long = -1
)
