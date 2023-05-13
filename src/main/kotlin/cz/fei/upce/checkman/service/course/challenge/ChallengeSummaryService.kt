package cz.fei.upce.checkman.service.course.challenge

import cz.fei.upce.checkman.domain.statistic.ChallengeSummary
import cz.fei.upce.checkman.repository.review.statistic.ChallengeSummaryRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ChallengeSummaryService(
    private val challengeSummaryRepository: ChallengeSummaryRepository
) {
    fun findByAppUserAndChallenge(appUserId: Long, challengeId: Long): Flux<ChallengeSummary> {
        return challengeSummaryRepository.findAllByAppUserIdEqualsAndChallengeIdEquals(appUserId, challengeId)
    }

}
