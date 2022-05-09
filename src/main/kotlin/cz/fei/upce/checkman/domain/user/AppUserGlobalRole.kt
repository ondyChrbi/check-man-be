package cz.fei.upce.checkman.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("app_user_global_role")
data class AppUserGlobalRole(
    @Id var id: Long? = null,
    var appUserId: Long? = null,
    var globalRoleId: Long? = null
)
