package cz.fei.upce.checkman.dto.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.dto.ResponseDto
import java.time.LocalDateTime

data class CourseSemesterResponseDtoV1(
    var id: Long? = null,
    var note: String? = null,
    var dateStart: LocalDateTime? = null,
    var dateEnd: LocalDateTime? = null
) : ResponseDto<CourseSemester, CourseSemesterResponseDtoV1>() {
    override fun withId(id: Long?): CourseSemesterResponseDtoV1 {
        this.id = id
        return this
    }

    override fun toEntity() = CourseSemester(id, note, dateStart, dateEnd)

    fun toEntity(courseId: Long) = CourseSemester(id, note, dateStart, dateEnd, courseId)

    fun toEntity(courseDto: CourseResponseDtoV1) = CourseSemester(id, note, dateStart, dateEnd, courseDto.id)

    override fun toEntity(entity: CourseSemester): CourseSemester {
        entity.note = note
        entity.dateStart = dateStart
        entity.dateEnd = dateEnd

        return entity
    }

    companion object {
        fun fromEntity(courseSemester: CourseSemester) = CourseSemesterResponseDtoV1(
            courseSemester.id,
            courseSemester.note,
            courseSemester.dateStart,
            courseSemester.dateEnd
        )
    }
}
