package cz.fei.upce.checkman.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("global_role")
data class GlobalRole(
    @Id var id: Long? = null,
    var name: String? = null
) {
    companion object {
        const val ROLE_GLOBAL_ROLE_VIEW = "ROLE_GLOBAL_ROLE_VIEW"
        const val ROLE_GLOBAL_ROLE_MANAGE = "ROLE_GLOBAL_ROLE_MANAGE"
        const val ROLE_COURSE_MANAGE = "ROLE_COURSE_MANAGE"
        const val ROLE_COURSE_SEMESTER_MANAGE = "ROLE_COURSE_SEMESTER_MANAGE"
        const val ROLE_COURSE_VIEW = "ROLE_COURSE_VIEW"
        const val ROLE_COURSE_SEMESTER_VIEW = "ROLE_COURSE_SEMESTER_VIEW"
    }
}
