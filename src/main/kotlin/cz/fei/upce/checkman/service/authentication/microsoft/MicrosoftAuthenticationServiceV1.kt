package cz.fei.upce.checkman.service.authentication.microsoft

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.microsoft.MicrosoftMeResponseDtoV1
import cz.fei.upce.checkman.dto.security.authentication.AuthenticationResponseDtoV1
import cz.fei.upce.checkman.dto.security.authentication.MicrosoftAuthTokenResponseDtoV1
import cz.fei.upce.checkman.dto.security.authentication.MicrosoftOAuthResponseDtoV1
import cz.fei.upce.checkman.service.authentication.AuthenticationService
import cz.fei.upce.checkman.service.authentication.AuthenticationService.Companion.MAIL_DELIMITER
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class MicrosoftAuthenticationServiceV1(
    private val webClient: WebClient,
    private val authenticationService: AuthenticationServiceV1
) {
    @Value("\${login.provider.oauth2.microsoft.authorization_endpoint}")
    private lateinit var authenticationEndpoint: String

    @Value("\${login.provider.oauth2.microsoft.token_endpoint}")
    private lateinit var tokenEndpoint: String

    @Value("\${third_party.provider.api.microsoft.me_endpoint}")
    private lateinit var meEndpoint: String

    @Value("\${login.provider.oauth2.microsoft.client_id}")
    private lateinit var clientId: String

    @Value("\${login.provider.oauth2.microsoft.client_secret}")
    private lateinit var clientSecret: String

    @Value("\${login.provider.oauth2.microsoft.response_type}")
    private lateinit var responseType: String

    @Value("\${login.provider.oauth2.microsoft.redirect_uri}")
    private lateinit var redirectUri: String

    @Value("\${login.provider.oauth2.microsoft.scopes}")
    private lateinit var scopes: Array<String>

    @Value("\${login.permit.domains}")
    private lateinit var permitEmails: Array<String>

    fun createRedirectRequest(redirectUri: String?) = ResponseEntity<MicrosoftOAuthResponseDtoV1>(MicrosoftOAuthResponseDtoV1(
        UriComponentsBuilder.fromHttpUrl(authenticationEndpoint)
            .queryParam("client_id", clientId)
            .queryParam("response_type", responseType)
            .queryParam("redirect_uri", redirectUri ?: this.redirectUri)
            .queryParam("scope", scopes.joinToString(SCOPES_SEPARATOR))
            .buildAndExpand()
            .toUri()
            .toString()
    ), HttpStatus.OK)

    fun finish(code: String, redirectURI: String?): Mono<AuthenticationResponseDtoV1> {
        return retrieveAuthToken(code, redirectURI)
            .flatMap(this::retrievePersonalInfo)
            .flatMap(this::checkValidStagCredentials)
            .flatMap(this::authenticate)
            .log()
    }

    private fun retrieveAuthToken(code: String, redirectUri: String?): Mono<MicrosoftAuthTokenResponseDtoV1> {
        log.info("Contacting authentication API with code: $code")

        return webClient.post()
            .uri(tokenEndpoint)
            .body(
                BodyInserters.fromFormData("grant_type", "authorization_code")
                    .with("client_id", clientId)
                    .with("code", code)
                    .with("scope", scopes.joinToString(SCOPES_SEPARATOR))
                    .with("redirect_uri", redirectUri ?: this.redirectUri)
                    .with("client_secret", clientSecret)
            ).retrieve()
            .bodyToMono(MicrosoftAuthTokenResponseDtoV1::class.java)
    }

    private fun retrievePersonalInfo(authTokenRequest: MicrosoftAuthTokenResponseDtoV1): Mono<MicrosoftMeResponseDtoV1> {
        if (authTokenRequest.accessToken == null || authTokenRequest.accessToken.isEmpty()) {
            return Mono.error(MicrosoftEmptyAccessTokenException())
        }

        log.info("Getting user personal info with access token: ${authTokenRequest.accessToken}")

        return webClient.get()
            .uri(meEndpoint)
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${authTokenRequest.accessToken}")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .retrieve()
            .bodyToMono(MicrosoftMeResponseDtoV1::class.java)
    }

    private fun checkValidStagCredentials(meResponse: MicrosoftMeResponseDtoV1): Mono<MicrosoftMeResponseDtoV1> {
        if (isNotValidCredentials(meResponse)) {
            return Mono.error(MicrosoftNotValidUserCredentialsException("userPrincipalName"))
        }

        val domain = meResponse.userPrincipalName!!.substringAfter(MAIL_DELIMITER)
        if (!permitEmails.contains(domain)) {
            return Mono.error(NotUniversityMicrosoftAccountException(domain))
        }

        log.info("Microsoft authentication request by principal name ${meResponse.userPrincipalName} successful")

        return Mono.just(meResponse)
    }

    private fun authenticate(meResponse: MicrosoftMeResponseDtoV1): Mono<AuthenticationResponseDtoV1> {
        return authenticationService.authenticate(
            AppUser(
                stagId = AuthenticationService.extractStagId(meResponse.userPrincipalName!!),
                mail = meResponse.mail!!,
                displayName = meResponse.displayName!!,
                lastAccessDate = LocalDateTime.now()
            )
        )
    }

    private companion object {
        const val SCOPES_SEPARATOR = " "

        val log: Logger = LoggerFactory.getLogger(this::class.java)

        fun isNotValidCredentials(meResponse: MicrosoftMeResponseDtoV1) = !isValidCredentials(meResponse)

        fun isValidCredentials(meResponse: MicrosoftMeResponseDtoV1) =
            meResponse.userPrincipalName != null && meResponse.userPrincipalName.isNotBlank()
                    && meResponse.userPrincipalName.contains(MAIL_DELIMITER)
                    && meResponse.displayName != null && meResponse.displayName.isNotBlank()
                    && meResponse.mail != null && meResponse.mail.isNotBlank()

    }
}
