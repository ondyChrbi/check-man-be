package cz.fei.upce.checkman.repository.review

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.review.Requirement
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface RequirementRepository : ReactiveCrudRepository<Requirement, Long> {
    fun findAllByChallengeIdEqualsAndActiveEquals(challengeId: Long, active: Boolean = true, pageable: Pageable = PageRequest.of(CheckManApplication.DEFAULT_PAGE, CheckManApplication.DEFAULT_PAGE_SIZE)): Flux<Requirement>

    fun findAllByChallengeIdEqualsAndRemovedEquals(challengeId: Long, removed: Boolean = false): Flux<Requirement>

    fun findAllByChallengeIdEquals(challengeId: Long) : Flux<Requirement>

    @Query("update requirement set removed = :removed where id = :id returning *")
    fun updateRemovedByIdEqualsReturnAll(id: Long, removed: Boolean = true): Mono<Requirement>

    @Query("""
        update requirement set active = false where id = :id returning *
    """)
    fun disableRequirement(id: Long) : Flux<Requirement>
    @Query("""
        select * from requirement req
        inner join requirement_review rr on req.id = rr.requirement_id
        inner join review r on rr.review_id = r.id
        where r.solution_id = :solutionId
    """)
    fun findAllBySolutionIdEquals(solutionId: Long) : Flux<Requirement>

    @Query("""
        select * from requirement r
        inner join requirement_review rr on r.id = rr.requirement_id
        where rr.id = :id
    """)
    fun findByReviewedRequirementId(id: Long) : Mono<Requirement>

    @Query("""
        select * from requirement r
        inner join requirement_review rr on r.id = rr.requirement_id
        where rr.review_id = :id
    """)
    fun findAllByReviewId(id: Long): Flux<Requirement>
}