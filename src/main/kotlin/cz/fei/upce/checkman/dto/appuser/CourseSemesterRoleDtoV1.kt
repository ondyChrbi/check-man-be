package cz.fei.upce.checkman.dto.appuser

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.dto.ResponseDto

data class CourseSemesterRoleDtoV1 (
    var id: Long? = -1,
    var name: String = ""
): ResponseDto<CourseSemesterRole, CourseSemesterRoleDtoV1>() {
    override fun withId(id: Long?): CourseSemesterRoleDtoV1 {
        this.id = id
        return this
    }

    override fun toEntity() = CourseSemesterRole(name = name)

    override fun toEntity(entity: CourseSemesterRole): CourseSemesterRole {
        entity.name = name
        return entity
    }

    companion object {
        fun fromEntity(role: CourseSemesterRole) = CourseSemesterRoleDtoV1(role.id, role.name)
    }
}
