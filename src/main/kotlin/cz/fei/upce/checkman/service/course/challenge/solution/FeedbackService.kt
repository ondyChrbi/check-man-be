package cz.fei.upce.checkman.service.course.challenge.solution

import cz.fei.upce.checkman.domain.review.Feedback
import cz.fei.upce.checkman.dto.graphql.input.course.challenge.solution.FeedbackInputQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL
import cz.fei.upce.checkman.repository.review.FeedbackRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FeedbackService(
    private val feedbackRepository: FeedbackRepository
) {
    fun create(feedback: cz.fei.upce.checkman.dto.graphql.input.course.challenge.solution.FeedbackInputQL): Mono<Feedback> {
        return feedbackRepository.save(feedback.toEntity())
    }

    fun findByTestResultId(testResultId: Long): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL> {
        return feedbackRepository.findAllByTestResult(testResultId)
            .map { it.toQL() }
    }

    fun findAllByReviewIdAsQL(id: Long): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL> {
        return feedbackRepository.findAllByReview(id)
            .map { it.toQL() }
    }
}
