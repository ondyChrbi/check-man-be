package cz.fei.upce.checkman.dto.appuser

import cz.fei.upce.checkman.domain.user.GlobalRole
import cz.fei.upce.checkman.dto.ResponseDto

data class GlobalRoleResponseDtoV1 (
    var id: Long? = -1,
    var name: String = ""
): ResponseDto<GlobalRole, GlobalRoleResponseDtoV1>() {
    override fun withId(id: Long?): GlobalRoleResponseDtoV1 {
        this.id = id
        return this
    }

    override fun toEntity() = GlobalRole(name = name)

    override fun toEntity(entity: GlobalRole): GlobalRole {
        entity.name = name

        return entity
    }

    companion object {
        fun fromEntity(globalRole: GlobalRole) = GlobalRoleResponseDtoV1(globalRole.id, globalRole.name)
    }
}