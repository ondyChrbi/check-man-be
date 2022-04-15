package cz.fei.upce.checkman.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("app_user_course_semester_role")
data class AppUserCourseSemesterRole(
    @Id var id : Long? = null,
    var appUser : AppUser? = null,
    var courseSemesterRole: CourseSemesterRole? = null,
    var courseSemester : CourseSemester? = null
)