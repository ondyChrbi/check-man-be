package cz.fei.upce.checkman.dto.graphql.output.appuser

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterRoleQL
import cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterRolesQL
import java.time.LocalDateTime

data class AppUserQL (
    val id: Long? = null,
    val stagId: String = "",
    val mail: String = "",
    val displayName: String = "",
    val registrationDate: LocalDateTime = LocalDateTime.now(),
    val lastAccessDate: LocalDateTime = LocalDateTime.now(),
    val disabled: Boolean = false,
    val globalRoles: MutableList<GlobalRoleQL> = mutableListOf(),
    val courseRoles: MutableList<CourseSemesterRolesQL> = mutableListOf(),
    val roles: MutableList<CourseSemesterRoleQL> = mutableListOf()
) {
    fun toEntity(): AppUser {
        return AppUser(
            id = this.id,
            stagId = this.stagId,
            mail = this.mail,
            displayName = this.displayName,
            registrationDate = this.registrationDate,
            lastAccessDate = this.lastAccessDate,
            disabled = this.disabled
        )
    }
}
