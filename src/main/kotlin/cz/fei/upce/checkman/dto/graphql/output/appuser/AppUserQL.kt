package cz.fei.upce.checkman.dto.graphql.output.appuser

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
    val globalRoles: MutableList<cz.fei.upce.checkman.dto.graphql.output.appuser.GlobalRoleQL> = mutableListOf(),
    val courseRoles: MutableList<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterRolesQL> = mutableListOf(),
    val roles: MutableList<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterRoleQL> = mutableListOf()
)
