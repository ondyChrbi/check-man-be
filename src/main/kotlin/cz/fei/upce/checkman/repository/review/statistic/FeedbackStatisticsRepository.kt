package cz.fei.upce.checkman.repository.review.statistic

import cz.fei.upce.checkman.domain.statistic.FeedbackStatistics
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface FeedbackStatisticsRepository : ReactiveCrudRepository<FeedbackStatistics, Long> {
    fun findAllBySemesterIdEquals(semesterId: Long, sort: Sort = DEFAULT_SORT, pageable: PageRequest = DEFAULT_PAGEABLE) : Flux<FeedbackStatistics>

    companion object {
        val DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "count")
        val DEFAULT_PAGEABLE = PageRequest.of(0, 5)
    }
}