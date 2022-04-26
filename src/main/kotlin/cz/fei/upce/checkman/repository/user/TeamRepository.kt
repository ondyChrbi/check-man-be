package cz.fei.upce.checkman.repository.user

import cz.fei.upce.checkman.domain.user.Team
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TeamRepository : ReactiveCrudRepository<Team, Long> {
    @Query("""
        select * from team t 
        inner join app_user_team aut on t.id = aut.team_id 
        where aut.app_user_id = :appUserId and t.private = true""")
    fun findPersonalTeam(appUserId: Long) : Flux<Team>
}