package cz.fei.upce.checkman.dto.graphql.output.challenge

import cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestConfigurationQL
import java.time.OffsetDateTime

data class ChallengeQL (
    var id: Long? = null,
    var name: String = "",
    var description: String = "",
    var deadlineDate: OffsetDateTime? = null,
    var startDate: OffsetDateTime? = null,
    var active: Boolean = true,
    var published: Boolean = false,
    var author: AppUserQL? = null,
    var requirements: List<RequirementQL> = emptyList(),
    var testConfigurations: List<TestConfigurationQL> = emptyList(),
    var challengeKind: String = "",
)