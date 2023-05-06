package cz.fei.upce.checkman.service.course.challenge.solution

import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestResultQL
import cz.fei.upce.checkman.repository.challenge.solution.TestResultRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TestResultService(private val testResultRepository: TestResultRepository) {
    fun findById(id: Long): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestResultQL> {
        return testResultRepository.findById(id)
            .map { it.toDto() }
    }

    fun findBySolutionAsQL(solutionId: Long): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestResultQL> {
        return testResultRepository.findFirstBySolutionIdEquals(solutionId)
            .map { it.toDto() }
    }

    fun checkExistById(id: Long): Mono<Boolean> {
        return testResultRepository.existsById(id)
            .flatMap {
                if (!it) Mono.error(ResourceNotFoundException()) else Mono.just(it)
            }
    }
}