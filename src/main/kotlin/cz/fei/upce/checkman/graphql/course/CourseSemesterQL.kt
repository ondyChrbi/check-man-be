package cz.fei.upce.checkman.graphql.course

import java.time.LocalDateTime

data class CourseSemesterQL(
    var id: Long,
    var note: String,
    var dateStart: LocalDateTime,
    var dateEnd: LocalDateTime,
)
