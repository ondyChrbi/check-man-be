package cz.fei.upce.checkman.service

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.security.authentication.AuthenticationRequestDtoV1
import cz.fei.upce.checkman.repository.user.AppUserRepository
import org.springframework.stereotype.Service

@Service
class AppUserServiceV1(private val appUserRepository: AppUserRepository) {
    fun findUser(authenticationRequest: AuthenticationRequestDtoV1) =
        appUserRepository.findByStagIdEquals(authenticationRequest.stagId)

    fun save(appUser: AppUser) =
        appUserRepository.save(appUser)
}
