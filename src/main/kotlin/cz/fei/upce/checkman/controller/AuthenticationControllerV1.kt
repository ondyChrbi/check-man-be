package cz.fei.upce.checkman.controller

import cz.fei.upce.checkman.dto.security.AuthenticationRequestDtoV1
import cz.fei.upce.checkman.service.AuthenticationServiceV1
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@RestController
@RequestMapping("/v1/authentication")
class AuthenticationControllerV1(private val authenticationService : AuthenticationServiceV1) {
    @PostMapping("/login")
    fun login(@Validated @RequestBody authRequest: AuthenticationRequestDtoV1) =
        authenticationService.authenticate(authRequest)
            .map { ResponseEntity.ok(it) }
            .switchIfEmpty { Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()) }
            .log()
}
