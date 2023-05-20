package cz.fei.upce.checkman.service.course.challenge.solution

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.review.Feedback
import cz.fei.upce.checkman.dto.graphql.input.course.challenge.solution.FeedbackInputQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL
import cz.fei.upce.checkman.repository.review.FeedbackRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class FeedbackService(
    private val feedbackRepository: FeedbackRepository
) {
    fun create(feedback: FeedbackInputQL): Mono<Feedback> {
        return feedbackRepository.findFirstByDescriptionEquals(feedback.description)
            .switchIfEmpty(feedbackRepository.save(feedback.toEntity()))
    }

    fun findByTestResultId(testResultId: Long): Flux<FeedbackQL> {
        return feedbackRepository.findAllByTestResult(testResultId)
            .map { it.toQL() }
    }

    fun findAllByReviewIdAsQL(
        id: Long,
        pageSize: Int? = CheckManApplication.DEFAULT_PAGE_SIZE,
        page: Int? = CheckManApplication.DEFAULT_PAGE,
    ): Flux<FeedbackQL> {
        return feedbackRepository.findAllByReview(id, pageSize ?: CheckManApplication.DEFAULT_PAGE_SIZE, page ?: CheckManApplication.DEFAULT_PAGE)
            .map { it.toQL() }
    }

    fun findById(id: Long): Mono<Feedback> {
        return feedbackRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
    }

    fun deleteById(id: Long): Mono<Boolean> {
        val exist = feedbackRepository.existsById(id)

        return exist.flatMap {
            if (it) {
                feedbackRepository.deleteById(id)
                    .then(Mono.just(true))
            } else {
                Mono.error(ResourceNotFoundException())
            }
        }
    }
}
