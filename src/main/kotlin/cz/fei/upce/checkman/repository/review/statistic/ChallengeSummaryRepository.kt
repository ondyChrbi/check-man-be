package cz.fei.upce.checkman.repository.review.statistic

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.statistic.ChallengeSummary
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ChallengeSummaryRepository : ReactiveSortingRepository<ChallengeSummary, Long> {
    fun findAllByAppUserIdEqualsAndChallengeIdEquals(appUserId: Long, challengeId: Long, pageable: Pageable = CheckManApplication.DEFAULT_PAGEABLE): Flux<ChallengeSummary>
}