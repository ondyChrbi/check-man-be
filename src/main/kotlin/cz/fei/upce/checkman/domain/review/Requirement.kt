package cz.fei.upce.checkman.domain.review

import cz.fei.upce.checkman.domain.challenge.Challenge
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("requirement")
data class Requirement(
    @Id var id: Long? = null,
    var name: String? = null,
    var description: String? = null,
    var minPoint: Short? = null,
    var maxPoint: Short? = null,
    var challenge: Challenge? = null
)