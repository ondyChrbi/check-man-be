package cz.fei.upce.checkman.controller.appuser

import cz.fei.upce.checkman.doc.appuser.MeEndpointV1
import cz.fei.upce.checkman.dto.appuser.AppUserResponseDtoV1
import cz.fei.upce.checkman.service.appuser.AppUserServiceV1
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/app-user")
class AppUserControllerV1(
    private val appUserServiceV1: AppUserServiceV1,
    private val authenticationService: AuthenticationServiceV1
    ) {
    @GetMapping("/me")
    @MeEndpointV1
    fun me(authentication: Authentication?): Mono<ResponseEntity<AppUserResponseDtoV1>> {
        return appUserServiceV1.me(authenticationService.extractAuthenticateUser(authentication!!))
            .flatMap { assignSelfRef(it) }
            .map { ResponseEntity.ok(it) }
    }

    private fun assignSelfRef(appUserDto: AppUserResponseDtoV1): Mono<AppUserResponseDtoV1> =
        WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(this::class.java).me(null))
            .withSelfRel()
            .toMono()
            .map { appUserDto.add(it) }
}