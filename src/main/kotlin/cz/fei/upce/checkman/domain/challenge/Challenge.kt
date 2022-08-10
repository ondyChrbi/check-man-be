package cz.fei.upce.checkman.domain.challenge

import cz.fei.upce.checkman.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.graphql.output.challenge.ChallengeQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

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
    fun toQL(author: AppUserQL) = ChallengeQL(
        id, name, description, deadlineDate, startDate, author
    )
}