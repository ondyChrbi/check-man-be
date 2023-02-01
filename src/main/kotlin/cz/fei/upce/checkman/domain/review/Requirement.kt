package cz.fei.upce.checkman.domain.review

import cz.fei.upce.checkman.graphql.output.challenge.requirement.RequirementQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("requirement")
data class Requirement(
    @Id var id: Long? = null,
    var name: String = "",
    var description: String = "",
    var active: Boolean = true,
    var minPoint: Short = 0,
    var maxPoint: Short = 0,
    var challengeId: Long = -1,
    var removed: Boolean = false
) {
    fun toQL() = RequirementQL(id!!, name, description, active, minPoint.toInt(), maxPoint.toInt())
}