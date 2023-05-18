package cz.fei.upce.checkman.service.course.challenge

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.statistic.ChallengeSummary
import cz.fei.upce.checkman.repository.review.statistic.ChallengeSummaryRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ChallengeSummaryService(
    private val challengeSummaryRepository: ChallengeSummaryRepository
) {
    fun findByAppUserAndChallenge(appUserId: Long, challengeId: Long, limit: Int = CheckManApplication.DEFAULT_LIMIT, offset: Int = CheckManApplication.DEFAULT_OFFSET): Flux<ChallengeSummary> {
        val pageable = PageRequest.of(offset, limit)

        return challengeSummaryRepository.findAllByAppUserIdEqualsAndChallengeIdEquals(
            appUserId, challengeId, pageable
        )
    }

}
