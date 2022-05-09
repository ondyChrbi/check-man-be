package cz.fei.upce.checkman.component.security

import cz.fei.upce.checkman.service.appuser.AppUserServiceV1
import cz.fei.upce.checkman.service.role.GlobalRoleServiceV1
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class AuthenticationManager(
    private val jwtUtil: JWTUtil,
    private val appUserServiceV1: AppUserServiceV1,
    private val globalRoleServiceV1: GlobalRoleServiceV1
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val authToken = authentication?.credentials.toString()
        val username = jwtUtil.parseUsernameFromToken(authToken)

        return Mono.just(jwtUtil.isValid(authToken))
            .filter { it == true }
            .switchIfEmpty { Mono.empty() }
            .flatMap { appUserServiceV1.findUser(username) }
            .switchIfEmpty { Mono.empty() }
            .flatMap { globalRoleServiceV1.rolesByUser(it).collectList() }
            .map { it.map { role -> SimpleGrantedAuthority(role.name) } }
            .map { UsernamePasswordAuthenticationToken(username, null, it) }
    }
}