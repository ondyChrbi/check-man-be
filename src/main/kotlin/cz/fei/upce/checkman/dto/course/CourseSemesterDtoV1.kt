package cz.fei.upce.checkman.dto.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.dto.BaseDto
import java.time.LocalDateTime

data class CourseSemesterDtoV1(
    var id: Long? = null,
    var note: String? = null,
    var startDate: LocalDateTime? = null,
    var endDate: LocalDateTime? = null,
) : BaseDto<CourseSemester, CourseSemesterDtoV1> {
    override fun withId(id: Long?): CourseSemesterDtoV1 {
        this.id = id
        return this
    }

    override fun toEntity() = CourseSemester(id, note, startDate, endDate)

    fun toEntity(courseDto: CourseDtoV1) = CourseSemester(id, note, startDate, endDate, courseDto.id)

    companion object {
        fun fromEntity(courseSemester: CourseSemester) = CourseSemesterDtoV1(
            courseSemester.id,
            courseSemester.note,
            courseSemester.dateStart,
            courseSemester.dateEnd
        )
    }
}
