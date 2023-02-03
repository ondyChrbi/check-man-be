package cz.fei.upce.checkman.repository.challenge

import cz.fei.upce.checkman.domain.challenge.Solution
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface SolutionRepository : ReactiveCrudRepository<Solution, Long> {
    fun findAllByChallengeIdEqualsAndUserIdEquals(challengeId: Long, userId: Long) : Flux<Solution>

    fun findFirstByIdEqualsAndUserIdEquals(id: Long, userId: Long) : Mono<Solution>

    @Query("""
        select s.* from solution s
        left outer join review r on s.id = r.solution_id
        left outer join challenge c on s.challenge_id = c.id
        where r.id IS NULL and c.id = :challengeId
    """)
    fun findAllWithoutReview(challengeId: Long): Flux<Solution>

    @Query("""
        select count(*) from solution s
        left outer join review r on s.id = r.solution_id
        left outer join challenge c on s.challenge_id = c.id
        where r.id IS NULL and c.id = :challengeId
    """)
    fun countAllWithoutReview(challengeId: Long): Mono<Long>
}