package cz.fei.upce.checkman.handler

import cz.fei.upce.checkman.dto.security.authentication.AuthenticationRequestDtoV1
import cz.fei.upce.checkman.service.AuthenticationServiceV1
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class AuthenticationHandlerV1(private val authenticationServiceV1: AuthenticationServiceV1) {
    fun login(request : ServerRequest) =
        request.bodyToMono(AuthenticationRequestDtoV1::class.java)
            .flatMap { authenticationServiceV1.authenticate(it) }
            .flatMap {
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(it))
            }
            .switchIfEmpty { ServerResponse.status(HttpStatus.UNAUTHORIZED).build() }
}