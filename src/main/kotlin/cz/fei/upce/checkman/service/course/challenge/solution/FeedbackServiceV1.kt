package cz.fei.upce.checkman.service.course.challenge.solution

import cz.fei.upce.checkman.domain.review.Feedback
import cz.fei.upce.checkman.graphql.input.course.challenge.solution.FeedbackInputQL
import cz.fei.upce.checkman.repository.review.FeedbackRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FeedbackServiceV1(
    private val feedbackRepository: FeedbackRepository
) {
    fun create(feedback: FeedbackInputQL): Mono<Feedback> {
        return feedbackRepository.save(feedback.toEntity())
    }
}
