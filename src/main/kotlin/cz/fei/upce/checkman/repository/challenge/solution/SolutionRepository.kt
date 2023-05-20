package cz.fei.upce.checkman.repository.challenge.solution

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.challenge.solution.Solution
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

    @Query(value = """
        select distinct s.* from solution s
        left outer join review r on s.id = r.solution_id
        left outer join challenge c on s.challenge_id = c.id
        where (r.id is null or r.published = false) and c.id = :challengeId
        limit :size offset :offset
    """
    )
    fun findAllToReview(challengeId: Long, offset: Int = CheckManApplication.DEFAULT_PAGE, size: Int = CheckManApplication.DEFAULT_PAGE_SIZE): Flux<Solution>

    @Query("""
        select count(distinct s.*) from solution s
        left outer join review r on s.id = r.solution_id
        left outer join challenge c on s.challenge_id = c.id
        where (r.id is null or r.published = false) and c.id = :challengeId
    """)
    fun countAllWithoutReview(challengeId: Long): Mono<Long>

    @Query("""
        select s.* from solution s 
        inner join review r on s.id = r.solution_id
        where r.id = :reviewId limit 1
    """)
    fun findByReview(reviewId: Long) : Mono<Solution>

    @Query("""
        select s.* from solution s
        where s.challenge_id = :challengeId
        limit :size offset :offset
    """)
    fun findAllByChallengeIdEquals(challengeId: Long, offset: Int? = CheckManApplication.DEFAULT_PAGE, size: Int? = CheckManApplication.DEFAULT_PAGE_SIZE): Flux<Solution>

    @Query("""
        select s.* from solution s
        where s.user_id = :appUserId
        limit :size offset :offset
    """)
    fun findAllByAppUserIdEquals(appUserId: Long, offset: Int? = CheckManApplication.DEFAULT_PAGE, size: Int? = CheckManApplication.DEFAULT_PAGE_SIZE): Flux<Solution>
}