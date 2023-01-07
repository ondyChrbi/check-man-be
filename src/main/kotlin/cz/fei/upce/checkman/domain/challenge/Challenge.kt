package cz.fei.upce.checkman.domain.challenge

import cz.fei.upce.checkman.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.graphql.output.challenge.ChallengeQL
import cz.fei.upce.checkman.graphql.output.challenge.requirement.RequirementQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.ZoneOffset

@Table("challenge")
data class Challenge(
    @Id var id: Long? = null,
    var name: String = "",
    var description: String = "",
    var deadlineDate: LocalDateTime? = null,
    var startDate: LocalDateTime? = null,
    var authorId: Long = 0,
    var courseSemesterId: Long? = null,
    var challengeKindId: Long = 0
) {
    fun toQL(author: AppUserQL, requirements: List<RequirementQL> = emptyList()) = ChallengeQL(
        id, name, description, deadlineDate?.atOffset(ZoneOffset.UTC), startDate?.atOffset(ZoneOffset.UTC), author, requirements, ChallengeKind.Value.IDS_MAP[challengeKindId].toString()
    )

    fun isPermissionNeeded(): Boolean {
        return NEED_PERMISSIONS_KINDS_IDS.contains(challengeKindId)
    }

    companion object {
        val NEED_PERMISSIONS_KINDS_IDS = arrayOf(ChallengeKind.Value.CREDIT.id, ChallengeKind.Value.EXAM.id)
    }
}