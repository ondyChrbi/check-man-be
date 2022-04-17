package cz.fei.upce.checkman.component.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class AuthenticationManager(private val jwtUtil: JWTUtil) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val authToken = authentication?.credentials.toString()
        val username = jwtUtil.parseUsernameFromToken(authToken)

        return Mono.just(jwtUtil.isValid(authToken))
            .filter {it == true}
            .switchIfEmpty { Mono.empty() }
            .map { UsernamePasswordAuthenticationToken(username, null) }
    }
}