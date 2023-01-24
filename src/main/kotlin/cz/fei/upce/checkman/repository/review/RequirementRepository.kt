package cz.fei.upce.checkman.repository.review

import cz.fei.upce.checkman.domain.review.Requirement
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface RequirementRepository : ReactiveCrudRepository<Requirement, Long> {
    fun findAllByChallengeIdEqualsAndActiveEquals(challengeId: Long, active: Boolean = true): Flux<Requirement>

    @Query("""
        update requirement set active = false where id = :id returning *
    """)
    fun disableRequirement(id: Long) : Flux<Requirement>
}