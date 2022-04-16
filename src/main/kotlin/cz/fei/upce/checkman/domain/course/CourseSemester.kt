package cz.fei.upce.checkman.domain.course

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("course_semester")
data class CourseSemester(
    @Id var id: Long? = null,
    var note: String? = null,
    var startDate: LocalDateTime? = null,
    var endDate: LocalDateTime? = null,
    var course: Course? = null
)