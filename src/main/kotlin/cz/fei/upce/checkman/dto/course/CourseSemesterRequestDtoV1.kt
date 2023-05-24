package cz.fei.upce.checkman.dto.course

import cz.fei.upce.checkman.dto.RequestDto
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

data class CourseSemesterRequestDtoV1(
    var note: String? = null,
    @field:NotNull(message = "{course.semester.date-start.not-null}")
    var dateStart: LocalDateTime? = null,
    @field:NotNull(message = "{course.semester.date-end.not-null}")
    var dateEnd: LocalDateTime? = null
) : RequestDto<CourseSemesterRequestDtoV1, CourseSemesterResponseDtoV1> {
    override fun toResponseDto() = CourseSemesterResponseDtoV1(
        note = note,
        dateStart = dateStart,
        dateEnd = dateEnd
    )

    override fun preventNullCollections(): CourseSemesterRequestDtoV1 = this
}
