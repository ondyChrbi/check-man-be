package cz.fei.upce.checkman.service.course.challenge.solution

import cz.fei.upce.checkman.graphql.output.challenge.solution.TestResultQL
import cz.fei.upce.checkman.repository.challenge.TestResultRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TestResultServiceV1(private val testResultRepository: TestResultRepository) {
    fun findById(id: Long): Mono<TestResultQL> {
        return testResultRepository.findById(id)
            .map { it.toDto() }
    }

    fun findBySolutionAsQL(solutionId: Long): Mono<TestResultQL> {
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