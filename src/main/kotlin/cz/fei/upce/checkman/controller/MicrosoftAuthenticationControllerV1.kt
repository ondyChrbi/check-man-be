package cz.fei.upce.checkman.controller

import cz.fei.upce.checkman.doc.MicrosoftAuthenticationStartEndpointV1
import cz.fei.upce.checkman.service.authentication.microsoft.MicrosoftAuthenticationServiceV1
import cz.fei.upce.checkman.service.authentication.microsoft.MicrosoftEmptyAccessTokenException
import cz.fei.upce.checkman.service.authentication.microsoft.MicrosoftNotValidUserCredentialsException
import cz.fei.upce.checkman.service.authentication.microsoft.NotUniversityMicrosoftAccountException
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/authentication/microsoft")
@Tag(name = "Microsoft Authentication V1", description = "Microsoft Authentication API (V1)")
class MicrosoftAuthenticationControllerV1(private val microsoftAuthenticationService: MicrosoftAuthenticationServiceV1) {
    @GetMapping("/start")
    @MicrosoftAuthenticationStartEndpointV1
    fun start() = Mono.just(microsoftAuthenticationService.createRedirectRequest())

    @GetMapping("/finish")
    fun finish(
        @RequestParam(required = true) code: String, @RequestParam(required = false) state: String?,
        @RequestParam(required = false) adminConsent: String?, @RequestParam(required = false) error: String?,
        @RequestParam(required = false) errorDescription: String?
    ) = microsoftAuthenticationService.finish(code)

    @ExceptionHandler(value = [MicrosoftEmptyAccessTokenException::class])
    fun sPValidationException(ex : MicrosoftEmptyAccessTokenException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.message)
    }

    @ExceptionHandler(value = [MicrosoftNotValidUserCredentialsException::class])
    fun sPValidationException(ex : MicrosoftNotValidUserCredentialsException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.message)
    }

    @ExceptionHandler(value = [NotUniversityMicrosoftAccountException::class])
    fun sPValidationException(ex : NotUniversityMicrosoftAccountException): ResponseEntity<*> {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.message)
    }
}