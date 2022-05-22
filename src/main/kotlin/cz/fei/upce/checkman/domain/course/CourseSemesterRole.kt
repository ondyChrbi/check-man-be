package cz.fei.upce.checkman.domain.course

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("course_semester_role")
data class CourseSemesterRole(
    @Id var id: Long? = null,
    var name: String = ""
)
