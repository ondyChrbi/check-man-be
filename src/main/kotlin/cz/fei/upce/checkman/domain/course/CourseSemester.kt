package cz.fei.upce.checkman.domain.course

import cz.fei.upce.checkman.graphql.output.course.CourseSemesterQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("course_semester")
data class CourseSemester(
    @Id var id: Long? = null,
    var note: String? = null,
    var dateStart: LocalDateTime? = null,
    var dateEnd: LocalDateTime? = null,
    var courseId: Long? = null
) {
    fun toQL() = CourseSemesterQL(id!!, note!!, dateStart!!, dateEnd!!)
}