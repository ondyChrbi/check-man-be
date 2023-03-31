package cz.fei.upce.checkman.repository.user

import cz.fei.upce.checkman.domain.user.GlobalRole
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface GlobalRoleRepository : ReactiveCrudRepository<GlobalRole, Long> {
    fun findFirstByNameEquals(name: String): Mono<GlobalRole>

    @Query("""
        select gr.* from global_role gr
        inner join app_user_global_role augr on gr.id = augr.global_role_id
        where augr.app_user_id = :appUserId
    """)
    fun findAllByAppUser(appUserId: Long): Flux<GlobalRole>
}