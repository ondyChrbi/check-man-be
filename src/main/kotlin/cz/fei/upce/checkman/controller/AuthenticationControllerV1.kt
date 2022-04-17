package cz.fei.upce.checkman.controller

import cz.fei.upce.checkman.dto.security.AuthenticationRequestDtoV1
import cz.fei.upce.checkman.dto.security.AuthenticationResponseDtoV1
import cz.fei.upce.checkman.service.AuthenticationServiceV1
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/authentication")
class AuthenticationControllerV1(private val authenticationService : AuthenticationServiceV1) {
    @PostMapping("/login")
    fun login(@Validated @RequestBody authRequest: AuthenticationRequestDtoV1) : Mono<ResponseEntity<AuthenticationResponseDtoV1>> = TODO("Not implemented yet")
}