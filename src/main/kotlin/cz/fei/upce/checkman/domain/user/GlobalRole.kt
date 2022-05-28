package cz.fei.upce.checkman.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("global_role")
data class GlobalRole(
    @Id var id: Long? = null,
    var name: String = ""
) {
    companion object {
        const val ROLE_GLOBAL_ROLE_VIEW = "ROLE_GLOBAL_ROLE_VIEW"
        const val ROLE_GLOBAL_ROLE_MANAGE = "ROLE_GLOBAL_ROLE_MANAGE"
        const val ROLE_COURSE_MANAGE = "ROLE_COURSE_MANAGE"
        const val ROLE_COURSE_SEMESTER_MANAGE = "ROLE_COURSE_SEMESTER_MANAGE"
        const val ROLE_COURSE_VIEW = "ROLE_COURSE_VIEW"
        const val ROLE_COURSE_SEMESTER_VIEW = "ROLE_COURSE_SEMESTER_VIEW"
        const val ROLE_COURSE_CHALLENGE_VIEW = "ROLE_COURSE_VIEW"
        const val ROLE_COURSE_CHALLENGE_MANAGE = "ROLE_COURSE_SEMESTER_VIEW"
        const val ROLE_VIEW_APP_USER = "ROLE_VIEW_APP_USER"
        const val ROLE_MANAGE_APP_USER = "ROLE_MANAGE_APP_USER"
        const val ROLE_BLOCK_APP_USER = "ROLE_BLOCK_APP_USER"
        const val ROLE_UNBLOCK_APP_USER = "ROLE_BLOCK_APP_USER"
    }
}
