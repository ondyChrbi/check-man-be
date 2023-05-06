package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.domain.review.Feedback
import cz.fei.upce.checkman.dto.graphql.input.course.challenge.solution.FeedbackInputQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.ReviewQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestResultQL
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
    fun createFeedback(@Argument feedback: cz.fei.upce.checkman.dto.graphql.input.course.challenge.solution.FeedbackInputQL): Mono<Feedback> {
        return feedbackService.create(feedback)
    }

    @SchemaMapping(typeName = "TestResult", field = "feedbacks")
    fun testResultsFeedbacks(testResult: cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestResultQL?) : Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL> {
        if (testResult?.id == null) {
            return Flux.empty()
        }

        return testResultService.checkExistById(testResult.id).flatMapMany {
            feedbackService.findByTestResultId(testResult.id)
        }
    }

    @SchemaMapping(typeName = "Review", field = "feedbacks")
    fun reviewFeedbacks(review: cz.fei.upce.checkman.dto.graphql.output.challenge.solution.ReviewQL): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL> {
        return feedbackService.findAllByReviewIdAsQL(review.id)
    }
}