package cz.fei.upce.checkman.domain.review

import cz.fei.upce.checkman.dto.graphql.input.course.challenge.ReviewInputQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.ReviewQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("review")
data class Review(
    @Id var id: Long? = null,
    var description: String? = "",
    var solutionId: Long? = -1L,
    var appUserId: Long? = -1L,
    var reviewTemplateId: Long? = null,
    var active: Boolean = true,
    var published: Boolean = false,
) {
    fun toQL(requirements: List<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL> = listOf(), feedbacks: List<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL> = listOf()): cz.fei.upce.checkman.dto.graphql.output.challenge.solution.ReviewQL {
        return cz.fei.upce.checkman.dto.graphql.output.challenge.solution.ReviewQL(
            id!!,
            description,
            requirements,
            feedbacks,
            active,
            published
        )
    }

    fun update(input: cz.fei.upce.checkman.dto.graphql.input.course.challenge.ReviewInputQL): Review {
        description = input.description
        return this
    }
}
