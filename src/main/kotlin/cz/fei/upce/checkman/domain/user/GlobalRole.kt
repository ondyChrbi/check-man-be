package cz.fei.upce.checkman.domain.user

import cz.fei.upce.checkman.dto.graphql.output.appuser.GlobalRoleQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority

@Table("global_role")
data class GlobalRole(
    @Id var id: Long? = null,
    var name: String = ""
): GrantedAuthority {

    override fun getAuthority() = this.name

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GlobalRole

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString() = this.name
    fun toQL() = cz.fei.upce.checkman.dto.graphql.output.appuser.GlobalRoleQL(id, name)

    companion object {
        const val ROLE_GLOBAL_ROLE_VIEW = "ROLE_GLOBAL_ROLE_VIEW"
        const val ROLE_GLOBAL_ROLE_MANAGE = "ROLE_GLOBAL_ROLE_MANAGE"
        const val ROLE_COURSE_ACCESS = "ROLE_COURSE_ACCESS"
        const val ROLE_CHALLENGE_ACCESS = "ROLE_CHALLENGE_ACCESS"
        const val ROLE_COURSE_MANAGE = "ROLE_COURSE_MANAGE"
        const val ROLE_COURSE_SEMESTER_MANAGE = "ROLE_COURSE_SEMESTER_MANAGE"
        const val ROLE_COURSE_VIEW = "ROLE_COURSE_VIEW"
        const val ROLE_COURSE_SEMESTER_VIEW = "ROLE_COURSE_SEMESTER_VIEW"
        const val ROLE_COURSE_CHALLENGE_VIEW = "ROLE_COURSE_CHALLENGE_VIEW"
        const val ROLE_COURSE_CHALLENGE_MANAGE = "ROLE_COURSE_SEMESTER_VIEW"
        const val ROLE_VIEW_APP_USER = "ROLE_VIEW_APP_USER"
        const val ROLE_MANAGE_APP_USER = "ROLE_MANAGE_APP_USER"
        const val ROLE_BLOCK_APP_USER = "ROLE_BLOCK_APP_USER"
        const val ROLE_UNBLOCK_APP_USER = "ROLE_BLOCK_APP_USER"
    }
}
