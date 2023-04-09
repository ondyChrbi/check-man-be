package cz.fei.upce.checkman.repository.challenge.solution

import cz.fei.upce.checkman.domain.challenge.solution.TestConfiguration
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TestConfigurationRepository : ReactiveCrudRepository<TestConfiguration, Long> {
    fun findAllByChallengeIdEquals(challengeId: Long): Flux<TestConfiguration>
}