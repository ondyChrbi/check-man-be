package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.domain.review.Feedback
import cz.fei.upce.checkman.graphql.input.course.challenge.solution.FeedbackInputQL
import cz.fei.upce.checkman.service.course.challenge.solution.FeedbackServiceV1
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Mono

@Controller
@Validated
class FeedbackQLController(
    private val feedbackService: FeedbackServiceV1
) {
    @MutationMapping
    fun createFeedback(@Argument feedback: FeedbackInputQL): Mono<Feedback> {
        return feedbackService.create(feedback)
    }
}