package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.domain.review.Feedback
import cz.fei.upce.checkman.graphql.input.course.challenge.solution.FeedbackInputQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.FeedbackQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.ReviewQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.TestResultQL
import cz.fei.upce.checkman.service.course.challenge.solution.FeedbackService
import cz.fei.upce.checkman.service.course.challenge.solution.TestResultService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
@Validated
class FeedbackQLController(
    private val testResultService: TestResultService,
    private val feedbackService: FeedbackService
) {
    @MutationMapping
    fun createFeedback(@Argument feedback: FeedbackInputQL): Mono<Feedback> {
        return feedbackService.create(feedback)
    }

    @SchemaMapping(typeName = "TestResult", field = "feedbacks")
    fun testResultsFeedbacks(testResult: TestResultQL?) : Flux<FeedbackQL> {
        if (testResult?.id == null) {
            return Flux.empty()
        }

        return testResultService.checkExistById(testResult.id).flatMapMany {
            feedbackService.findByTestResultId(testResult.id)
        }
    }

    @SchemaMapping(typeName = "Review", field = "feedbacks")
    fun reviewFeedbacks(review: ReviewQL): Flux<FeedbackQL> {
        return feedbackService.findAllByReviewIdAsQL(review.id)
    }
}