package cz.fei.upce.checkman.dto.course

import java.time.LocalDateTime

data class CourseSemesterDtoV1(
    var id: Long? = null,
    var note: String? = null,
    var startDate: LocalDateTime? = null,
    var endDate: LocalDateTime? = null,
)
