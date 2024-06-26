package cz.fei.upce.checkman.repository.challenge.solution

import cz.fei.upce.checkman.domain.challenge.solution.TestResult
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface TestResultRepository : ReactiveCrudRepository<TestResult, Long> {
    fun findFirstBySolutionIdEquals(solutionId: Long) : Mono<TestResult>
}