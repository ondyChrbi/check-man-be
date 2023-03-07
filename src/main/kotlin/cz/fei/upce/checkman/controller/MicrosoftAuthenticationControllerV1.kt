package cz.fei.upce.checkman.controller

import cz.fei.upce.checkman.doc.authentication.microsoft.MicrosoftAuthenticationExchangeEndpointV1
import cz.fei.upce.checkman.dto.security.authentication.AuthenticationResponseDtoV1
import cz.fei.upce.checkman.service.authentication.ExchangeRequestDtoV1
import cz.fei.upce.checkman.service.authentication.microsoft.MicrosoftAuthenticationServiceV1
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/authentication/microsoft")
@Tag(name = "Microsoft Authentication V1", description = "Microsoft Authentication API (V1)")
class MicrosoftAuthenticationControllerV1(private val microsoftAuthenticationService: MicrosoftAuthenticationServiceV1) {
    @CrossOrigin
    @PostMapping("/exchange")
    @MicrosoftAuthenticationExchangeEndpointV1
    fun exchange(@RequestBody exchangeRequest: ExchangeRequestDtoV1): Mono<AuthenticationResponseDtoV1> {
        return microsoftAuthenticationService.exchange(exchangeRequest)
    }

}