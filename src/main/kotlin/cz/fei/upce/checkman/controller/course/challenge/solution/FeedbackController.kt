package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.review.Feedback
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL
import cz.fei.upce.checkman.service.course.challenge.solution.FeedbackService
import cz.fei.upce.checkman.service.course.challenge.solution.TestResultService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
@Validated
class FeedbackController(
    private val testResultService: TestResultService,
    private val feedbackService: FeedbackService
) {
    @QueryMapping
    fun feedback(
        @Argument id: Long,
    ): Mono<FeedbackQL> {
        return feedbackService.findById(id)
            .map { it.toQL() }
    }

    @QueryMapping
    fun feedbacks(
        @Argument reviewId: Long,
        @Argument pageSize: Int? = CheckManApplication.DEFAULT_PAGE_SIZE,
        @Argument page: Int? = CheckManApplication.DEFAULT_PAGE,
    ): Flux<FeedbackQL> {
        return feedbackService.findAllByReviewIdAsQL(reviewId, pageSize, page)
    }

    @MutationMapping
    fun deleteFeedback(id: Long): Mono<Boolean> {
        return feedbackService.deleteById(id)
    }

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