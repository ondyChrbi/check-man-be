package cz.fei.upce.checkman.service.course.challenge

import cz.fei.upce.checkman.domain.challenge.Challenge
import cz.fei.upce.checkman.domain.challenge.PermittedAppUserChallenge
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.graphql.output.challenge.PermittedAppUserChallengeQL
import cz.fei.upce.checkman.repository.challenge.PermittedAppUserChallengeRepository
import cz.fei.upce.checkman.service.appuser.AppUserService
import cz.fei.upce.checkman.service.course.challenge.exception.AppUserCanAlreadyAccessChallengeException
import cz.fei.upce.checkman.service.course.challenge.exception.AppUserCanNotAccessChallengeException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class PermitChallengeService(
    private val permittedPermittedAppUserChallengeRepository: PermittedAppUserChallengeRepository,
    private val appUserService: AppUserService,
) {
    fun permitToAccessAsQL(challengeId: Long, appUserId: Long, accessTo: LocalDateTime): Mono<PermittedAppUserChallengeQL> {
        val checkExist = checkNotExist(challengeId, appUserId)

        return checkExist.flatMap {
            permittedPermittedAppUserChallengeRepository.save(
                PermittedAppUserChallenge(appUserId = appUserId, challengeId = challengeId, accessTo = accessTo)
            )
        }.map { it.toQL() }

    }

    fun removeAccessFrom(challengeId: Long, appUserId: Long): Mono<Boolean> {
        val checkExist = checkExist(challengeId, appUserId)

        return checkExist.flatMap {
            permittedPermittedAppUserChallengeRepository.existsByAppUserIdEqualsAndChallengeIdEquals(appUserId, challengeId)
                .flatMap {
                    permittedPermittedAppUserChallengeRepository.deleteAllByAppUserIdEqualsAndChallengeIdEquals(
                        appUserId,
                        challengeId
                    )
                }
        }
    }

    fun findAllToPermitAsQL(challengeId: Long, search: String = ""): Flux<AppUserQL> {
        val disable = disableAccessToOutdated(challengeId)

         return disable.flatMapMany {
                 appUserService.searchAllPermitToChallenge(challengeId, search)
                     .map { it.toQL() }
             }
    }

    fun finaAllPermittedAsQL(challengeId: Long, search: String = ""): Flux<AppUserQL> {
        val disable = disableAccessToOutdated(challengeId)

        return disable.flatMapMany {
                appUserService.searchAllPermittedToChallenge(challengeId, search)
                    .map { it.toQL() }
            }
    }

    fun isPermitted(challenge: Challenge, appUser: AppUser): Mono<Boolean> {
        return checkExist(challenge.id!!, appUser.id!!)
    }


    private fun disableAccessToOutdated(challengeId: Long): Mono<Boolean> {
        val outdated = permittedPermittedAppUserChallengeRepository.findAllByChallengeIdEqualsAndAccessToBefore(challengeId)

        return outdated
            .flatMap { permittedPermittedAppUserChallengeRepository.delete(it) }
            .collectList()
            .map { true }
    }

    private fun checkExist(challengeId: Long, appUserId: Long): Mono<Boolean> {
        return permittedPermittedAppUserChallengeRepository.existsByAppUserIdEqualsAndChallengeIdEquals(appUserId, challengeId)
            .flatMap {
                if (!it) {
                    Mono.error(AppUserCanNotAccessChallengeException(challengeId, appUserId))
                } else {
                    Mono.just(true)
                }
            }
    }

    private fun checkNotExist(challengeId: Long, appUserId: Long): Mono<Boolean> {
        return permittedPermittedAppUserChallengeRepository.existsByAppUserIdEqualsAndChallengeIdEquals(appUserId, challengeId)
            .flatMap {
                if (it) {
                    Mono.error(AppUserCanAlreadyAccessChallengeException(challengeId, appUserId))
                } else {
                    Mono.just(true)
                }
            }
    }
}
