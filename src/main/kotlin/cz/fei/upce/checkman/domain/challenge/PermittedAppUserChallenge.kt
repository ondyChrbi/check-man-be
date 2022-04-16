package cz.fei.upce.checkman.domain.challenge

import cz.fei.upce.checkman.domain.user.AppUser
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("permitted_app_user_challenge")
data class PermittedAppUserChallenge(
    @Id var id: Long? = null,
    var accessTo: LocalDateTime? = null,
    var appUser: AppUser? = null,
    var challenge: Challenge? = null
)
