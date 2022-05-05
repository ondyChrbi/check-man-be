package cz.fei.upce.checkman.service.authentication

import cz.fei.upce.checkman.component.security.JWTUtil
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.security.authentication.AuthenticationRequestDtoV1
import cz.fei.upce.checkman.dto.security.authentication.AuthenticationResponseDtoV1
import cz.fei.upce.checkman.service.AppUserServiceV1
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime

@Service
class AuthenticationServiceV1(private val userService: AppUserServiceV1, private val jwtUtil: JWTUtil) :
    AuthenticationService {
    fun authenticate(authenticationRequest: AuthenticationRequestDtoV1) = userService.findUser(authenticationRequest)
        .map { AuthenticationResponseDtoV1(jwtUtil.generateToken(authenticationRequest.stagId)) }
        .log()

    fun authenticate(stagId: String): Mono<AuthenticationResponseDtoV1> {
        log.info("Authentication user with stag id $stagId started")

        return userService.updateLastAccessDate(stagId)
            .map { AuthenticationResponseDtoV1(jwtUtil.generateToken(stagId)) }
    }

    fun authenticate(appUser: AppUser): Mono<AuthenticationResponseDtoV1> {
        return userService.findUser(appUser.stagId!!)
            .switchIfEmpty { register(appUser) }
            .flatMap { authenticate(appUser.stagId!!) }
    }

    fun register(appUser: AppUser): Mono<AppUser> {
        log.info("Register new user with stag id: ${appUser.stagId}")

        appUser.registrationDate = LocalDateTime.now()
        return userService.save(appUser)
    }

    private companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}
