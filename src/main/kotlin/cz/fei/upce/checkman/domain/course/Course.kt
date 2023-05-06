package cz.fei.upce.checkman.domain.course

import cz.fei.upce.checkman.dto.graphql.output.course.CourseQL
import cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterQL
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
    fun toQL(semesters: List<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterQL> = listOf()) =
        cz.fei.upce.checkman.dto.graphql.output.course.CourseQL(
            id, stagId, name, dateCreation, icon, template, semesters
        )
}
