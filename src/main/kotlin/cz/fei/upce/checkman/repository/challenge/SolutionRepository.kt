package cz.fei.upce.checkman.repository.challenge

import cz.fei.upce.checkman.domain.challenge.Solution
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface SolutionRepository : ReactiveCrudRepository<Solution, Long> {
    fun findAllByChallengeIdEqualsAndUserIdEquals(challengeId: Long, userId: Long) : Flux<Solution>

    fun findFirstByIdEqualsAndUserIdEquals(id: Long, userId: Long) : Mono<Solution>
}