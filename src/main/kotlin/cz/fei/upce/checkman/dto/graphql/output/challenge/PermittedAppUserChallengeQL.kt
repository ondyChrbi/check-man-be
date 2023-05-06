package cz.fei.upce.checkman.dto.graphql.output.challenge

import cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL
import java.time.OffsetDateTime

data class PermittedAppUserChallengeQL(
    var id: Long? = null,
    var accessTo: OffsetDateTime? = null,
    var appUser: cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL? = null,
    var challenge: cz.fei.upce.checkman.dto.graphql.output.challenge.ChallengeQL? = null
)
