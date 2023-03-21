package cz.fei.upce.checkman.repository.review.statistic

import cz.fei.upce.checkman.domain.statistic.FeedbackStatistics
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface FeedbackStatisticsRepository : ReactiveCrudRepository<FeedbackStatistics, Long> {
    fun findAllBySemesterIdEquals(semesterId: Long) : Flux<FeedbackStatistics>
}