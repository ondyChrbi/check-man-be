package cz.fei.upce.checkman.domain.review

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("requirement")
data class Requirement(
    @Id var id: Long? = null,
    var name: String = "",
    var description: String = "",
    var minPoint: Short = 0,
    var maxPoint: Short = 0,
    var challengeId: Long = -1
)