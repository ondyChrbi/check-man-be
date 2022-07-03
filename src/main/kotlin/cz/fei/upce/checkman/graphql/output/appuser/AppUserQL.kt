package cz.fei.upce.checkman.graphql.output.appuser

import cz.fei.upce.checkman.graphql.output.course.CourseSemesterRolesQL
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
    val courseRoles: MutableList<CourseSemesterRolesQL> = mutableListOf()
)
