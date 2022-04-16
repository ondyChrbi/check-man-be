package cz.fei.upce.checkman.domain.challenge

import cz.fei.upce.checkman.domain.user.Team
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("solution")
data class Solution(
    @Id var id: Long? = null,
    var uploadDate: LocalDateTime? = null,
    var team: Team? = null,
    var status: Status? = null,
    var challenge: Challenge? = null
) {
    enum class Status(approve: Boolean) {
        APPROVED(true),
        RETURN_TO_EDIT(false),
        DENIED(false)
    }
}
