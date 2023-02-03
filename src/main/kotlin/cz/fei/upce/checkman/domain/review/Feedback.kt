package cz.fei.upce.checkman.domain.review

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("feedback")
data class Feedback(
    @Id var id: Long? = null,
    var description: String? = "",
    var feedbackTypeId: Long = -1,
    var reviewId: Long = -1
)
