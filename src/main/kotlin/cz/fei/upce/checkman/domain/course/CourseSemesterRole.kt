package cz.fei.upce.checkman.domain.course

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("course_semester_role")
data class CourseSemesterRole(
    @Id var id: Long? = null,
    var name: String = ""
) {
    enum class Value(val id: Long) {
        COURSE_ROLE_ACCESS(0),
        COURSE_ROLE_CREATE_CHALLENGE(1);

        fun toEntity() = CourseSemesterRole(id, COURSE_ROLE_ACCESS.toString())
    }
}
