package cz.fei.upce.checkman.domain.review

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("requirement_review")
data class RequirementReview(
    @Id var id: Long? = null,
    var point: Short? = null,
    var description: String? = null,
    var requirement: Requirement? = null,
    var review: Review? = null
)
