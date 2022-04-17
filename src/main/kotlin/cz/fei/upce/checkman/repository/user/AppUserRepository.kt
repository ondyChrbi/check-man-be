package cz.fei.upce.checkman.repository.user

import cz.fei.upce.checkman.domain.user.AppUser
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AppUserRepository : ReactiveCrudRepository<AppUser, Long> {
    fun findByStagIdEquals(stagId : String) : Mono<AppUser>
}