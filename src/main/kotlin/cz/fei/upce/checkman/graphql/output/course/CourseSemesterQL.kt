package cz.fei.upce.checkman.graphql.output.course

import cz.fei.upce.checkman.graphql.output.PageableQL
import cz.fei.upce.checkman.graphql.output.challenge.ChallengeQL
import java.time.LocalDateTime

data class CourseSemesterQL(
    var id: Long,
    var note: String,
    var dateStart: LocalDateTime,
    var dateEnd: LocalDateTime,
    var challenges: List<ChallengeQL> = emptyList(),
    override var page: Int? = null,
    override var pageSize: Int? = null
) : PageableQL(page, pageSize)
