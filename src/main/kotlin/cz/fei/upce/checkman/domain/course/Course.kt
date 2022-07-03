package cz.fei.upce.checkman.domain.course

import cz.fei.upce.checkman.graphql.course.CourseQL
import cz.fei.upce.checkman.graphql.course.CourseSemesterQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("course")
data class Course(
    @Id var id: Long? = null,
    var stagId: String = "",
    var name: String = "",
    var dateCreation: LocalDateTime = LocalDateTime.now(),
    var icon: String? = null,
    var template: String? = null
) {
    fun toQL(semesters: List<CourseSemesterQL>) = CourseQL(
        id, stagId, name, dateCreation, icon, template, semesters
    )
}
