package cz.fei.upce.checkman.repository.review

import cz.fei.upce.checkman.domain.review.RequirementReview
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface RequirementReviewRepository : ReactiveCrudRepository<RequirementReview, Long> {
    fun findAllByReviewIdEquals(reviewId: Long) : Flux<RequirementReview>

    fun findFirstByReviewIdEqualsAndRequirementIdEquals(reviewId: Long, requirementId: Long) : Mono<RequirementReview>

    fun existsByReviewIdEqualsAndRequirementIdEquals(reviewId: Long, requirementId: Long) : Mono<Boolean>

    fun findByReviewIdEqualsAndRequirementIdEquals(reviewId: Long, requirementId: Long) : Mono<RequirementReview>
}
