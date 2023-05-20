package cz.fei.upce.checkman.service.course.challenge.solution


import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestConfigurationQL
import cz.fei.upce.checkman.repository.challenge.solution.TestConfigurationRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class TestConfigurationService(
    private val testConfigurationRepository: TestConfigurationRepository
) {
    fun findAllByChallenge(challengeId: Long): Flux<TestConfigurationQL> {
        return testConfigurationRepository.findAllByChallengeIdEquals(challengeId)
            .map { it.toDto() }
    }

}
