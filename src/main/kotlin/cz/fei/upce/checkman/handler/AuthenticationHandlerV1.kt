package cz.fei.upce.checkman.handler

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.security.authentication.AuthenticationRequestDtoV1
import cz.fei.upce.checkman.dto.security.registration.RegistrationRequestDtoV1
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime

@Component
class AuthenticationHandlerV1(private val authenticationService: AuthenticationServiceV1) {
    fun login(request: ServerRequest) =
        request.bodyToMono(AuthenticationRequestDtoV1::class.java)
            .flatMap { authenticationService.authenticate(it) }
            .flatMap {
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(it))
            }
            .switchIfEmpty { ServerResponse.status(HttpStatus.UNAUTHORIZED).build() }

    fun register(request: ServerRequest) =
        request.bodyToMono(RegistrationRequestDtoV1::class.java)
            .flatMap {
                authenticationService.register(
                    AppUser(
                        stagId = it.stagId,
                        registrationDate = LocalDateTime.now(),
                        lastAccessDate = LocalDateTime.now(),
                        disabled = false
                    )
                )
            }.flatMap {
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(it))
            }
}