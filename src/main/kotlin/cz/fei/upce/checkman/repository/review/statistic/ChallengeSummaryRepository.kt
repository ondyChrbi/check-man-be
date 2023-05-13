package cz.fei.upce.checkman.repository.review.statistic

import cz.fei.upce.checkman.domain.statistic.ChallengeSummary
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ChallengeSummaryRepository : ReactiveCrudRepository<ChallengeSummary, Long> {
    fun findAllByAppUserIdEqualsAndChallengeIdEquals(appUserId: Long, challengeId: Long): Flux<ChallengeSummary>
}