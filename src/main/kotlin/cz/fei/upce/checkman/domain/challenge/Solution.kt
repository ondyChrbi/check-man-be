package cz.fei.upce.checkman.domain.challenge

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("solution")
data class Solution(
    @Id var id: Long? = null,
    var uploadDate: LocalDateTime? = null,
    var userId: Long = -1L,
    var statusId: Long = -1L,
    var challengeId: Long = -1L,
) {
    enum class Status(approve: Boolean) {
        APPROVED(true),
        RETURN_TO_EDIT(false),
        DENIED(false)
    }
}
