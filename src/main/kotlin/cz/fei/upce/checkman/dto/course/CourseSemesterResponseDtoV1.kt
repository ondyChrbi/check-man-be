package cz.fei.upce.checkman.dto.course

import cz.fei.upce.checkman.domain.course.Semester
import cz.fei.upce.checkman.dto.ResponseDto
import java.time.LocalDateTime

data class CourseSemesterResponseDtoV1(
    var id: Long? = null,
    var note: String? = null,
    var dateStart: LocalDateTime? = null,
    var dateEnd: LocalDateTime? = null
) : ResponseDto<Semester, CourseSemesterResponseDtoV1>() {
    override fun withId(id: Long?): CourseSemesterResponseDtoV1 {
        this.id = id
        return this
    }

    override fun toEntity() = Semester(id, note, dateStart, dateEnd)

    fun toEntity(courseId: Long) = Semester(id, note, dateStart, dateEnd, courseId)

    fun toEntity(courseDto: CourseResponseDtoV1) = Semester(id, note, dateStart, dateEnd, courseDto.id)

    override fun toEntity(entity: Semester): Semester {
        entity.note = note
        entity.dateStart = dateStart
        entity.dateEnd = dateEnd

        return entity
    }

    companion object {
        fun fromEntity(semester: Semester) = CourseSemesterResponseDtoV1(
            semester.id,
            semester.note,
            semester.dateStart,
            semester.dateEnd
        )
    }
}
