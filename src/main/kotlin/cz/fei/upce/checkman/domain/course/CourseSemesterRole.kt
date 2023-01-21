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
        CREATE_CHALLENGE(1),
        EDIT_CHALLENGE(2),
        SUBMIT_CHALLENGE_SOLUTION(3),
        DELETE_CHALLENGE(4);

        fun toEntity() = CourseSemesterRole(id, this.toString())

        companion object {
            val IDS_MAP = mapOf(
                0L to ACCESS,
                1L to CREATE_CHALLENGE,
                2L to EDIT_CHALLENGE,
                3L to SUBMIT_CHALLENGE_SOLUTION,
                4L to DELETE_CHALLENGE
            )
        }
    }
}
