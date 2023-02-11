package cz.fei.upce.checkman.domain.course

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("app_user_course_semester_role")
data class AppUserCourseSemesterRole(
    @Id var id: Long? = null,
    var appUserId: Long = -1,
    var courseSemesterRoleId: Long = -1,
    var courseSemesterId: Long = -1
)
