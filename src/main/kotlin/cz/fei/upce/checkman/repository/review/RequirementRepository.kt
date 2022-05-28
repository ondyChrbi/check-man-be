package cz.fei.upce.checkman.repository.review

import cz.fei.upce.checkman.domain.review.Requirement
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface RequirementRepository : ReactiveCrudRepository<Requirement, Long> {
    fun findAllByChallengeIdEquals(challengeId: Long): Flux<Requirement>
}