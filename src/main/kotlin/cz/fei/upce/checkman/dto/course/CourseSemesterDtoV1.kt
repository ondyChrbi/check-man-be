package cz.fei.upce.checkman.dto.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.dto.BaseDto
import java.time.LocalDateTime

data class CourseSemesterDtoV1(
    var id: Long? = null,
    var note: String? = null,
    var dateStart: LocalDateTime? = null,
    var dateEnd: LocalDateTime? = null,
) : BaseDto<CourseSemester, CourseSemesterDtoV1>() {
    override fun withId(id: Long?): CourseSemesterDtoV1 {
        this.id = id
        return this
    }

    override fun toEntity() = CourseSemester(id, note, dateStart, dateEnd)

    fun toEntity(courseDto: CourseDtoV1) = CourseSemester(id, note, dateStart, dateEnd, courseDto.id)

    override fun toEntity(entity: CourseSemester): CourseSemester {
        entity.note = note
        entity.dateStart = dateStart
        entity.dateEnd = dateEnd

        return entity
    }

    companion object {
        fun fromEntity(courseSemester: CourseSemester) = CourseSemesterDtoV1(
            courseSemester.id,
            courseSemester.note,
            courseSemester.dateStart,
            courseSemester.dateEnd
        )
    }
}
