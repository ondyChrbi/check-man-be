package cz.fei.upce.checkman.service.authentication.microsoft

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.microsoft.MicrosoftMeResponseDtoV1
import cz.fei.upce.checkman.dto.security.authentication.AuthenticationResponseDtoV1
import cz.fei.upce.checkman.service.authentication.AuthenticationService
import cz.fei.upce.checkman.service.authentication.AuthenticationService.Companion.MAIL_DELIMITER
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceImpl
import cz.fei.upce.checkman.service.authentication.ExchangeRequestDtoV1
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class MicrosoftAuthenticationService(
    private val webClient: WebClient,
    private val authenticationService: AuthenticationServiceImpl,
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

    fun exchange(exchangeRequest: ExchangeRequestDtoV1): Mono<AuthenticationResponseDtoV1> {
        return retrievePersonalInfo(exchangeRequest.authToken)
            .flatMap(this::checkValidStagCredentials)
            .flatMap(this::authenticate)
    }

    private fun retrievePersonalInfo(token: String): Mono<MicrosoftMeResponseDtoV1> {
        return webClient.get()
            .uri(meEndpoint)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
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
        val log: Logger = LoggerFactory.getLogger(this::class.java)

        fun isNotValidCredentials(meResponse: MicrosoftMeResponseDtoV1) = !isValidCredentials(meResponse)

        fun isValidCredentials(meResponse: MicrosoftMeResponseDtoV1) =
            meResponse.userPrincipalName != null && meResponse.userPrincipalName.isNotBlank()
                    && meResponse.userPrincipalName.contains(MAIL_DELIMITER)
                    && meResponse.displayName != null && meResponse.displayName.isNotBlank()
                    && meResponse.mail != null && meResponse.mail.isNotBlank()
    }
}
