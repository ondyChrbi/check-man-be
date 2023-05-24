package cz.fei.upce.checkman.component.security

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.service.appuser.AppUserService
import cz.fei.upce.checkman.service.authentication.DisabledUserException
import cz.fei.upce.checkman.service.role.GlobalRoleService
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class AuthenticationManager(
    private val jwtUtil: JWTUtil,
    private val appUserService: AppUserService,
    private val globalRoleService: GlobalRoleService
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val authToken = authentication?.credentials.toString()
        val username = jwtUtil.parseUsernameFromToken(authToken)

        return Mono.just(jwtUtil.isValid(authToken))
            .filter { it == true }
            .switchIfEmpty { Mono.empty() }
            .flatMap { appUserService.findByStagId(username) }
            .switchIfEmpty { Mono.empty() }
            .flatMap { if (it.disabled) Mono.error(DisabledUserException()) else Mono.just(it) }
            .flatMap { appUserService.updateLastAccessDate(it) }
            .flatMap { authenticateUserWithRoles(it) }
    }

    private fun authenticateUserWithRoles(user: AppUser): Mono<UsernamePasswordAuthenticationToken> {
        return globalRoleService.rolesByUser(user)
            .collectList()
            .map { UsernamePasswordAuthenticationToken(user, null, it) }
    }
}