package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.dto.graphql.output.challenge.ChallengeQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestConfigurationQL
import cz.fei.upce.checkman.service.course.challenge.solution.TestConfigurationService
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux

@Controller
class TestConfigurationController(
    private val testConfigurationService: TestConfigurationService,
) {
    @SchemaMapping(typeName = "Challenge")
    fun testConfigurations(challenge: ChallengeQL): Flux<TestConfigurationQL> {
        return testConfigurationService.findAllByChallenge(challenge.id!!)
    }
}