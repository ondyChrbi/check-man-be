package cz.fei.upce.checkman.repository.challenge

import cz.fei.upce.checkman.domain.challenge.Challenge
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ChallengeRepository : ReactiveCrudRepository<Challenge, Long> {
    fun existsByIdEqualsAndAndCourseSemesterIdEquals(id: Long, semesterId: Long): Mono<Boolean>
}