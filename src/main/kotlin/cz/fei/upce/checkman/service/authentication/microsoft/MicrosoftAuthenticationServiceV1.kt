package cz.fei.upce.checkman.service.authentication.microsoft

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.domain.user.AuthenticationExchange
import cz.fei.upce.checkman.domain.user.AuthenticationRequest
import cz.fei.upce.checkman.dto.microsoft.MicrosoftMeResponseDtoV1
import cz.fei.upce.checkman.dto.security.authentication.AuthenticationExchangeResponseDtoV1
import cz.fei.upce.checkman.dto.security.authentication.MicrosoftAuthTokenResponseDtoV1
import cz.fei.upce.checkman.dto.security.authentication.MicrosoftOAuthResponseDtoV1
import cz.fei.upce.checkman.service.authentication.AuthenticationService
import cz.fei.upce.checkman.service.authentication.AuthenticationService.Companion.MAIL_DELIMITER
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import cz.fei.upce.checkman.service.authentication.microsoft.exception.ExpiredOrNonExistingAuthenticationRequestKeyException
import cz.fei.upce.checkman.service.authentication.microsoft.exception.NotEqualsRedirectURIException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime

@Service
class MicrosoftAuthenticationServiceV1(
    private val webClient: WebClient,
    private val authenticationService: AuthenticationServiceV1,
    private val authenticationRequestOps: ReactiveRedisOperations<String, AuthenticationRequest>,
    private val exchangeRequestOps: ReactiveRedisOperations<String, AuthenticationExchange>
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

    @Value("\${check-man.security.authentication.microsoft.request-timeout}")
    private lateinit var timeout: String

    fun createRedirectRequest(redirectUri: String?): Mono<ResponseEntity<MicrosoftOAuthResponseDtoV1>> {
        val loginRequest = AuthenticationRequest(redirectUri = redirectUri ?: this.redirectUri)

        return authenticationRequestOps.opsForValue()
            .set(loginRequest.getRedisKey(), loginRequest, Duration.ofMillis(timeout.toLong()))
            .map {
                MicrosoftOAuthResponseDtoV1(
                    UriComponentsBuilder.fromHttpUrl(authenticationEndpoint)
                        .queryParam("client_id", clientId)
                        .queryParam("response_type", responseType)
                        .queryParam("redirect_uri", this.redirectUri)
                        .queryParam("scope", scopes.joinToString(SCOPES_SEPARATOR))
                        .queryParam("state", loginRequest.id)
                        .buildAndExpand()
                        .toUri()
                        .toString()
                )
            }.map { ResponseEntity.ok(it) }
    }

    fun finish(code: String, state: String, redirectURI: String): Mono<AuthenticationExchangeResponseDtoV1> {
        return retrieveAndCheckAuthRequest(state, redirectURI)
            .flatMap { retrieveAuthToken(code, redirectURI) }
            .flatMap(this::retrievePersonalInfo)
            .flatMap(this::checkValidStagCredentials)
            .flatMap(this::authenticate)
            .log()
    }

    private fun retrieveAndCheckAuthRequest(state: String, redirectURI: String): Mono<AuthenticationRequest> {
        return authenticationRequestOps.opsForValue().get(AuthenticationRequest.getRedisKey(state))
            .switchIfEmpty(Mono.error(ExpiredOrNonExistingAuthenticationRequestKeyException()))
            .flatMap { request ->
                if (request.redirectUri != redirectURI)
                    Mono.error(NotEqualsRedirectURIException())
                else
                    Mono.just(request)
            }
            .flatMap(this::removeAuthRequestFromCache)
    }

    private fun removeAuthRequestFromCache(authRequest: AuthenticationRequest): Mono<AuthenticationRequest> {
        return authenticationRequestOps.opsForValue()
            .delete(authRequest.getRedisKey())
            .map { authRequest }
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

    private fun authenticate(meResponse: MicrosoftMeResponseDtoV1): Mono<AuthenticationExchangeResponseDtoV1> {
        val requestingAppUser = AppUser(
            stagId = AuthenticationService.extractStagId(meResponse.userPrincipalName!!),
            mail = meResponse.mail!!,
            displayName = meResponse.displayName!!,
            lastAccessDate = LocalDateTime.now()
        )

        return authenticationService
            .authenticate(requestingAppUser)
            .flatMap { jwtInfo ->
                val expiration = Duration.ofMillis(timeout.toLong())
                val exchangeRequest = AuthenticationExchange(jwtTokenInfo = jwtInfo)

                exchangeRequestOps.opsForValue()
                    .set(exchangeRequest.getRedisKey(), exchangeRequest, expiration)
                    .map {
                        val issueAt = LocalDateTime.now()
                        val expiresAt = issueAt.plusSeconds(expiration.toSeconds())

                        AuthenticationExchangeResponseDtoV1(exchangeRequest.id, issueAt, expiresAt)
                    }
            }
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
