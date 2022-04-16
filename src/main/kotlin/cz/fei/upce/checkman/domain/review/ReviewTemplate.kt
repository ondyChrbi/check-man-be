package cz.fei.upce.checkman.domain.review

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("review_template")
data class ReviewTemplate(
    @Id var id : Long? = null,
    var template : String? = null
)
