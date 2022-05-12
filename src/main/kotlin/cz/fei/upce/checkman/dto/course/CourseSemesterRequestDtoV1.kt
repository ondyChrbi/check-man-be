package cz.fei.upce.checkman.dto.course

import cz.fei.upce.checkman.dto.RequestDto
import java.time.LocalDateTime

data class CourseSemesterRequestDtoV1(
    var note: String? = null,
    var dateStart: LocalDateTime? = null,
    var dateEnd: LocalDateTime? = null
) : RequestDto<CourseSemesterRequestDtoV1, CourseSemesterResponseDtoV1> {
    override fun toResponseDto() = CourseSemesterResponseDtoV1(
        note = note,
        dateStart = dateStart,
        dateEnd = dateEnd
    )

    override fun preventNullCollections(): CourseSemesterRequestDtoV1 = this
}
