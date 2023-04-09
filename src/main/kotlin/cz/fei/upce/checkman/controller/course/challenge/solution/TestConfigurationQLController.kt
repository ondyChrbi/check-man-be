package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.graphql.output.challenge.ChallengeQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.TestConfigurationQL
import cz.fei.upce.checkman.service.course.challenge.solution.TestConfigurationServiceV1
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux

@Controller
class TestConfigurationQLController(
    private val testConfigurationService: TestConfigurationServiceV1,
) {
    @SchemaMapping(typeName = "Challenge")
    fun testConfigurations(challenge: ChallengeQL): Flux<TestConfigurationQL> {
        return testConfigurationService.findAllByChallenge(challenge.id!!)
    }
}