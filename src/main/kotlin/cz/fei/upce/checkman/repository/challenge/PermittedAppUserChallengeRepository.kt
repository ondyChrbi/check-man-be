package cz.fei.upce.checkman.repository.challenge

import cz.fei.upce.checkman.domain.challenge.PermittedAppUserChallenge
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface PermittedAppUserChallengeRepository : ReactiveCrudRepository<PermittedAppUserChallenge, Long> {
    fun findFirstByAppUserIdEqualsAndChallengeIdEquals(appUserId: Long, challengeId: Long): Mono<PermittedAppUserChallenge>
    fun existsByAppUserIdEqualsAndChallengeIdEquals(appUserId: Long, challengeId: Long): Mono<Boolean>
    fun deleteAllByAppUserIdEqualsAndChallengeIdEquals(appUserId: Long, challengeId: Long): Mono<Boolean>
    fun findAllByChallengeIdEqualsAndAccessToBefore(challengeId: Long, accessTo: LocalDateTime = LocalDateTime.now()): Flux<PermittedAppUserChallenge>
}
