package cz.fei.upce.checkman.graphql.input.course.challenge.solution

import cz.fei.upce.checkman.domain.review.Feedback
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Validated
data class FeedbackInputQL(
    @field:NotBlank
    @field:NotEmpty
    @field:NotNull
    var description: String = "",
    @field:NotBlank
    @field:NotEmpty
    @field:NotNull
    var type: Feedback.FeedbackType = Feedback.FeedbackType.NEGATIVE
) {
    fun toEntity(): Feedback {
        return Feedback(description = description, feedbackTypeId =  type.id)
    }
}
