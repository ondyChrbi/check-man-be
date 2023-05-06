package cz.fei.upce.checkman.domain.course

import cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterQL
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
    fun toQL() = cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterQL(id!!, note!!, dateStart!!, dateEnd!!)

    fun isBeforeStart(nowDate: LocalDateTime = LocalDateTime.now()) = dateStart != null && nowDate.isBefore(dateStart)

    fun isAfterStart(nowDate: LocalDateTime = LocalDateTime.now()) = dateStart != null && nowDate.isAfter(dateStart)

    fun isBeforeEnd(nowDate: LocalDateTime = LocalDateTime.now()) = dateEnd != null && nowDate.isBefore(dateEnd)

    fun isAfterEnd(nowDate: LocalDateTime = LocalDateTime.now()) = dateEnd != null && nowDate.isAfter(dateEnd)

    enum class OrderByField {
        id,
        dateStart,
        dateEnd
    }
}