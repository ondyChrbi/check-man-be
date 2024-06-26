package cz.fei.upce.checkman.dto.graphql.output.challenge.solution

import cz.fei.upce.checkman.domain.challenge.solution.Solution
import cz.fei.upce.checkman.domain.user.AppUser
import java.time.OffsetDateTime

data class SolutionQL (
    var id: Long? = null,
    var uploadDate: OffsetDateTime? = null,
    var status: Solution.Status = Solution.Status.WAITING_TO_REVIEW,
    var review: ReviewQL? = null,
    var author: AppUser? = null
) {
    fun withReview(review: ReviewQL): SolutionQL {
        this.review = review
        return this
    }
}