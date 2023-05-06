package cz.fei.upce.checkman.dto.graphql.input.course.challenge

import cz.fei.upce.checkman.domain.review.Review
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Validated
data class ReviewInputQL(
    @field:NotNull
    @field:NotBlank
    @field:NotEmpty
    var description: String = ""
) {
    fun toEntity(solutionId: Long, appUserId: Long): Review {
        return Review(description = description, solutionId = solutionId, appUserId = appUserId)
    }
}
