package cz.fei.upce.checkman.service.course.challenge.solution

import cz.fei.upce.checkman.domain.review.Review
import cz.fei.upce.checkman.graphql.output.challenge.solution.ReviewQL
import cz.fei.upce.checkman.repository.review.ReviewRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ReviewServiceV1(
    private val reviewRepository: ReviewRepository
) {
    fun findAllBySolutionIdAsQL(solutionId: Long) : Mono<ReviewQL> {
        return reviewRepository.findFirstBySolutionIdEquals(solutionId)
            .map { it.toQL() }
    }

    fun findAllBySolutionId(solutionId: Long) : Mono<Review> {
        return reviewRepository.findFirstBySolutionIdEquals(solutionId)
    }
}