package cz.fei.upce.checkman.graphql.output.course

import cz.fei.upce.checkman.graphql.output.challenge.ChallengeQL
import java.time.LocalDateTime

data class CourseSemesterQL(
    var id: Long,
    var note: String,
    var dateStart: LocalDateTime,
    var dateEnd: LocalDateTime,
    var challenges: List<ChallengeQL> = emptyList()
)
