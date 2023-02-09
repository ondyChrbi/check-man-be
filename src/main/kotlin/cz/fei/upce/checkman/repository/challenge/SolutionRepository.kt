package cz.fei.upce.checkman.repository.challenge

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_OFFSET
import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_SIZE
import cz.fei.upce.checkman.domain.challenge.Solution
import org.springframework.data.domain.PageRequest
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
        where c.id = :challengeId
    """)
    fun findAll(challengeId: Long): Flux<Solution>

    @Query("""
        select distinct s.* from solution s
        left outer join review r on s.id = r.solution_id
        left outer join challenge c on s.challenge_id = c.id
        where (r.id is null or r.published = false) and c.id = :challengeId
    """)
    fun findAllToReview(challengeId: Long, pageable: PageRequest = PageRequest.of(DEFAULT_OFFSET, DEFAULT_SIZE)): Flux<Solution>

    @Query("""
        select distinct count(*) from solution s
        left outer join review r on s.id = r.solution_id
        left outer join challenge c on s.challenge_id = c.id
        where r.id IS NULL and c.id = :challengeId
    """)
    fun countAllWithoutReview(challengeId: Long): Mono<Long>
}