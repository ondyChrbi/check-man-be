package cz.fei.upce.checkman.repository.review.statistic

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.review.Feedback
import cz.fei.upce.checkman.domain.statistic.FeedbackStatistics
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface FeedbackStatisticsRepository : ReactiveCrudRepository<FeedbackStatistics, Long> {
    fun findDistinctBySemesterIdEqualsAndFeedbackTypeIdEquals(
        semesterId: Long,
        sort: Sort = DEFAULT_SORT,
        type: Long = Feedback.FeedbackType.POSITIVE.id,
        pageable: PageRequest = DEFAULT_PAGEABLE,
    ): Flux<FeedbackStatistics>

    fun findDistinctBySemesterIdEqualsAndDescriptionContainingIgnoreCaseAndFeedbackTypeIdEquals(
        semesterId: Long,
        description: String,
        sort: Sort = DEFAULT_SORT,
        type: Long = Feedback.FeedbackType.POSITIVE.id,
        pageable: PageRequest = DEFAULT_PAGEABLE,
    ): Flux<FeedbackStatistics>

    companion object {
        val DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "count")
        val DEFAULT_PAGEABLE = PageRequest.of(CheckManApplication.DEFAULT_PAGE, CheckManApplication.DEFAULT_PAGE_SIZE)
    }
}