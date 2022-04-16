package cz.fei.upce.checkman.domain.review

import cz.fei.upce.checkman.domain.challenge.Solution
import cz.fei.upce.checkman.domain.user.AppUser
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("review")
data class Review(
    @Id var id : Long? = null,
    var description : String? = null,
    var solution : Solution? = null,
    var author : AppUser? = null,
    var feedback : Feedback? = null,
    var reviewTemplate: ReviewTemplate? = null,
) {
    enum class Feedback {
        EXTREMELY_POSITIVE,
        POSITIVE,
        NEUTRAL,
        NEGATIVE,
    }
}
