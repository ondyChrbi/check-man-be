package cz.fei.upce.checkman.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("app_user_team")
data class AppUserTeam(
    @Id var id : Long? = null,
    var appUser: AppUser? = null,
    var team : Team? = null
)