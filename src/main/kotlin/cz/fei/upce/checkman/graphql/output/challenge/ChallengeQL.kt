package cz.fei.upce.checkman.graphql.output.challenge

import cz.fei.upce.checkman.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.graphql.output.challenge.requirement.RequirementQL
import java.time.LocalDateTime

data class ChallengeQL (
    var id: Long? = null,
    var name: String = "",
    var description: String = "",
    var deadlineDate: LocalDateTime? = null,
    var startDate: LocalDateTime? = null,
    var active: Boolean = true,
    var author: AppUserQL? = null,
    var requirements: List<RequirementQL> = emptyList(),
    var challengeKind: String = ""
)