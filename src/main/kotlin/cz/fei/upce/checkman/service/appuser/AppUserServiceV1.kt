package cz.fei.upce.checkman.service.appuser

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.security.authentication.AuthenticationRequestDtoV1
import cz.fei.upce.checkman.repository.user.AppUserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class AppUserServiceV1(
    private val appUserRepository: AppUserRepository,
    private val teamService: TeamServiceV1
) {
    fun findUser(authenticationRequest: AuthenticationRequestDtoV1) = findUser(authenticationRequest.stagId)

    fun findUser(stagId: String) = appUserRepository.findByStagIdEquals(stagId)

    fun updateLastAccessDate(appUser: AppUser): Mono<AppUser> {
        appUser.lastAccessDate = LocalDateTime.now()
        return appUserRepository.save(appUser)
    }

    fun updateLastAccessDate(stagId: String): Mono<AppUser> {
        return appUserRepository.findByStagIdEquals(stagId)
            .flatMap(this::updateLastAccessDate)
    }

    fun save(appUser: AppUser) = appUserRepository.save(appUser)
        .flatMap(teamService::createPersonalTeam)
        .flatMap { appUserRepository.findById(appUser.id!!) }
}
