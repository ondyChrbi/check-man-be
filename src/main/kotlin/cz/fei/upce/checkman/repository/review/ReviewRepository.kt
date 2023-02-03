package cz.fei.upce.checkman.repository.review

import cz.fei.upce.checkman.domain.review.Review
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ReviewRepository : ReactiveCrudRepository<Review, Long> {
    fun findFirstBySolutionIdEquals(solutionId: Long) : Mono<Review>
}