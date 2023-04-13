package cz.fei.upce.checkman.graphql.output.challenge

import cz.fei.upce.checkman.graphql.output.appuser.AppUserQL
import java.time.OffsetDateTime

data class PermittedAppUserChallengeQL(
    var id: Long? = null,
    var accessTo: OffsetDateTime? = null,
    var appUser: AppUserQL? = null,
    var challenge: ChallengeQL? = null
)
