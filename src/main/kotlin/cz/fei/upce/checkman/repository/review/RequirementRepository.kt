package cz.fei.upce.checkman.repository.review

import cz.fei.upce.checkman.domain.review.Requirement
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface RequirementRepository : ReactiveCrudRepository<Requirement, Long> {
    fun findAllByChallengeIdEquals(challengeId: Long): Flux<Requirement>

    fun findAllByChallengeIdEqualsAndRemovedEquals(challengeId: Long, removed: Boolean = false): Flux<Requirement>

    @Query("update requirement set removed = :removed where id = :id returning *")
    fun updateRemovedByIdEqualsReturnAll(id: Long, removed: Boolean = true): Mono<Requirement>
}