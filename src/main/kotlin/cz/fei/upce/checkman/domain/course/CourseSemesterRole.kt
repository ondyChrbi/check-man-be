package cz.fei.upce.checkman.domain.course

import cz.fei.upce.checkman.graphql.output.course.CourseSemesterRoleQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("course_semester_role")
data class CourseSemesterRole(
    @Id var id: Long? = null,
    var name: String = ""
) {
    fun toQL() = CourseSemesterRoleQL(id!!, name)

    enum class Value(val id: Long) {
        ACCESS(0),
        CREATE_CHALLENGE(1);

        fun toEntity() = CourseSemesterRole(id, this.toString())

        companion object {
            val IDS_MAP = mapOf(
                0L to ACCESS,
                1L to CREATE_CHALLENGE
            )
        }
    }
}
