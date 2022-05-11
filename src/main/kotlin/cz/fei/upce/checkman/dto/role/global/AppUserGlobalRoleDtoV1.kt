package cz.fei.upce.checkman.dto.role.global

import cz.fei.upce.checkman.domain.user.AppUserGlobalRole
import cz.fei.upce.checkman.dto.BaseDto
import org.springframework.data.annotation.Id

data class AppUserGlobalRoleDtoV1(
    @Id var id: Long? = null,
    var appUserId: Long? = null,
    var globalRoleId: Long? = null
) : BaseDto<AppUserGlobalRole, AppUserGlobalRoleDtoV1> {
    override fun withId(id: Long?): AppUserGlobalRoleDtoV1 {
        this.id = id
        return this
    }

    override fun toEntity() = AppUserGlobalRole(id, appUserId, globalRoleId)

    override fun toEntity(entity: AppUserGlobalRole): AppUserGlobalRole {
        entity.id = id
        entity.appUserId = appUserId
        entity.globalRoleId = globalRoleId

        return entity
    }
}
