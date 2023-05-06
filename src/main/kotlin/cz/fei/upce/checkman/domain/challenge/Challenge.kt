package cz.fei.upce.checkman.domain.challenge

import cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.ChallengeQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL
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
    var active: Boolean = true,
    var published: Boolean = false,
    var authorId: Long = 0,
    var courseSemesterId: Long? = null,
    var challengeKindId: Long = 0
) {
    fun toQL(author: cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL? = null, requirements: List<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL> = emptyList()) =
        cz.fei.upce.checkman.dto.graphql.output.challenge.ChallengeQL(
            id,
            name,
            description,
            deadlineDate?.atOffset(ZoneOffset.UTC),
            startDate?.atOffset(ZoneOffset.UTC),
            active,
            published,
            author,
            requirements,
            mutableListOf(),
            ChallengeKind.Value.IDS_MAP[challengeKindId].toString()
        )

    fun isPermissionNeeded(): Boolean {
        return NEED_PERMISSIONS_KINDS_IDS.contains(challengeKindId)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Challenge

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }


    companion object {
        val NEED_PERMISSIONS_KINDS_IDS = arrayOf(ChallengeKind.Value.CREDIT.id, ChallengeKind.Value.EXAM.id)
    }
}