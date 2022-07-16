package cz.fei.upce.checkman.controller

import cz.fei.upce.checkman.doc.authentication.microsoft.MicrosoftAuthenticationFinishEndpointV1
import cz.fei.upce.checkman.doc.authentication.microsoft.MicrosoftAuthenticationStartEndpointV1
import cz.fei.upce.checkman.service.authentication.microsoft.MicrosoftAuthenticationServiceV1
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/authentication/microsoft")
@Tag(name = "Microsoft Authentication V1", description = "Microsoft Authentication API (V1)")
class MicrosoftAuthenticationControllerV1(private val microsoftAuthenticationService: MicrosoftAuthenticationServiceV1) {
    @CrossOrigin
    @GetMapping("/start")
    @MicrosoftAuthenticationStartEndpointV1
    fun start(@RequestParam redirectURI: String?) =
        microsoftAuthenticationService.createRedirectRequest(redirectURI)

    @CrossOrigin
    @GetMapping("/finish")
    @MicrosoftAuthenticationFinishEndpointV1
    fun finish(
        @RequestParam redirectURI: String,
        @RequestParam(required = true) code: String, @RequestParam state: String,
        @RequestParam(required = false) adminConsent: String?, @RequestParam(required = false) error: String?,
        @RequestParam(required = false) errorDescription: String?
    ) = microsoftAuthenticationService.finish(code, state, redirectURI)
}