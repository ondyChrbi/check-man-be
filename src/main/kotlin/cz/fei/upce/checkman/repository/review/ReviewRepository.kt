package cz.fei.upce.checkman.repository.review

import cz.fei.upce.checkman.domain.review.Review
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ReviewRepository : ReactiveCrudRepository<Review, Long> {
    fun findFirstBySolutionIdEquals(solutionId: Long) : Mono<Review>

    @Query("""
        delete from feedback_review
        where review_id = :reviewId and feedback_id = :feedbackId
    """)
    fun unlinkFeedback(reviewId: Long, feedbackId: Long) : Mono<Void>

    @Query("""
        insert into feedback_review(feedback_id, review_id) 
        values (:feedbackId, :reviewId)
    """)
    fun linkFeedback(reviewId: Long, feedbackId: Long) : Mono<Void>

    @Query("""
        update review set published = true where id = :reviewId return *
    """)
    fun publish(reviewId: Long) : Flux<Review>
}