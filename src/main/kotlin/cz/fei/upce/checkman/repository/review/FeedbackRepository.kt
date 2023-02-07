package cz.fei.upce.checkman.repository.review

import cz.fei.upce.checkman.domain.review.Feedback
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface FeedbackRepository : ReactiveCrudRepository<Feedback, Long> {
    @Query("""
        select * from feedback f 
        inner join feedback_review fr on f.id = fr.feedback_id
        where review_id = :reviewId
    """)
    fun findAllByReviewIdEquals(reviewId: Long) : Flux<Feedback>
}
