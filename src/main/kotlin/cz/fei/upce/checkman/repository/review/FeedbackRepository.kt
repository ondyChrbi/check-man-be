package cz.fei.upce.checkman.repository.review

import cz.fei.upce.checkman.domain.review.Feedback
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface FeedbackRepository : ReactiveCrudRepository<Feedback, Long> {
    @Query("""
        select * from feedback f 
        inner join feedback_review fr on f.id = fr.feedback_id
        where review_id = :reviewId
    """)
    fun findAllByReviewIdEquals(reviewId: Long) : Flux<Feedback>

    @Query("""
        select * from feedback f
        inner join feedback_review fr on f.id = fr.feedback_id
        inner join review r on fr.review_id = r.id
        inner join solution s on r.solution_id = s.id
        inner join test_result tr on s.id = tr.solution_id
        where tr.id = :testResultId
    """)
    fun findAllByTestResult(testResultId: Long) : Flux<Feedback>

    @Query("""
        select f.* from feedback f 
        inner join feedback_review fr on f.id = fr.feedback_id
        where fr.review_id = :id
    """)
    fun findAllByReview(id: Long) : Flux<Feedback>
}
