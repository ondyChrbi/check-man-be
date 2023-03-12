package cz.fei.upce.checkman.domain.challenge

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.graphql.output.challenge.solution.ReviewQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.SolutionQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.ZoneOffset

@Table("solution")
data class Solution(
    @Id var id: Long? = null,
    var path: String = "",
    var uploadDate: LocalDateTime? = null,
    var userId: Long = -1L,
    var statusId: Long = -1L,
    var challengeId: Long = -1L,
) {

    fun toQL(review: ReviewQL) =
        SolutionQL(id, uploadDate?.atOffset(ZoneOffset.UTC), getById(statusId), review)

    fun toQL(review: ReviewQL? = null, author: AppUser? = null) =
        SolutionQL(id, uploadDate?.atOffset(ZoneOffset.UTC), getById(statusId), review, author)

    enum class Status(val approve: Boolean, val id: Long) {
        APPROVED(true, 0L),
        RETURN_TO_EDIT(false, 1L),
        DENIED(false, 2L),
        WAITING_TO_REVIEW(false, 3L)
    }

    companion object {

        val IDS_MAP = mapOf(
            0L to Status.APPROVED,
            1L to Status.RETURN_TO_EDIT,
            2L to Status.DENIED,
            3L to Status.WAITING_TO_REVIEW
        )

        fun getById(id: Long) = Status.values()[id.toInt()]
    }
}
