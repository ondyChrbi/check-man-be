package cz.fei.upce.checkman.graphql.input.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.graphql.InputQL
import java.time.LocalDateTime

data class SemesterInputQL (
    val note: String?,
    val dateStart: String,
    val dateEnd: String
): InputQL<CourseSemester> {
    override fun toEntity() = CourseSemester(note = note, dateStart = LocalDateTime.parse(dateStart), dateEnd = LocalDateTime.parse(dateEnd))

    fun toEntity(courseId: Long): CourseSemester {
        val courseSemester = this.toEntity()
        courseSemester.courseId = courseId

        return courseSemester
    }
}