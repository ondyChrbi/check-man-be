package cz.fei.upce.checkman.service.authentication

import cz.fei.upce.checkman.component.security.JWTUtil
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.domain.user.GlobalRole
import cz.fei.upce.checkman.dto.security.authentication.AuthenticationResponseDtoV1
import cz.fei.upce.checkman.service.appuser.AppUserServiceV1
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime

@Service
class AuthenticationServiceV1(private val userService: AppUserServiceV1, private val jwtUtil: JWTUtil) :
    AuthenticationService {

    fun authenticate(requestAppUser: AppUser): Mono<AuthenticationResponseDtoV1> {
        return userService.findUser(requestAppUser.stagId)
            .switchIfEmpty { register(requestAppUser) }
            .flatMap { appUser ->
                userService.updateLastAccessDate(appUser.stagId)
                    .flatMap { userService.meAsDto(it) }
                    .map { response ->
                        jwtUtil.generateTokenInfo(appUser.stagId)
                            .toAuthenticationResponseDtoV1(response)
                    }
            }
    }

    fun register(appUser: AppUser): Mono<AppUser> {
        log.info("Register new user with stag id: ${appUser.stagId}")

        appUser.registrationDate = LocalDateTime.now()
        return userService.save(appUser)
    }

    fun extractAuthenticateUser(authentication: Authentication): AppUser {
        if (authentication is UsernamePasswordAuthenticationToken && authentication.principal is AppUser) {
            return authentication.principal as AppUser
        }

        throw WrongSecurityPrincipalsException()
    }

    fun extractAuthorities(authentication: Authentication): Set<GlobalRole> {
        if (authentication is UsernamePasswordAuthenticationToken) {
            return authentication.authorities.map { it as GlobalRole }.toSet()
        }

        throw WrongSecurityPrincipalsException()
    }

    private companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
