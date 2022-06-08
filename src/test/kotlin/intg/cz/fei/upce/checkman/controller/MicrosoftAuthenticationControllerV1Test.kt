package cz.fei.upce.checkman.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.*
import cz.fei.upce.checkman.component.security.JWTUtil
import cz.fei.upce.checkman.dto.microsoft.MicrosoftMeResponseDtoV1
import cz.fei.upce.checkman.dto.security.authentication.AuthenticationResponseDtoV1
import cz.fei.upce.checkman.dto.security.authentication.MicrosoftAuthTokenResponseDtoV1
import cz.fei.upce.checkman.dto.security.authentication.MicrosoftOAuthResponseDtoV1
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@AutoConfigureWireMock(port = 8084)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class MicrosoftAuthenticationControllerV1Test {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var jwtUtil: JWTUtil

    @Value("\${login.provider.oauth2.microsoft.authorization_endpoint}")
    private lateinit var authenticationEndpoint: String

    @Value("\${login.provider.oauth2.microsoft.client_id}")
    private lateinit var clientId: String

    @Value("\${login.provider.oauth2.microsoft.response_type}")
    private lateinit var responseType: String

    @Value("\${login.provider.oauth2.microsoft.redirect_uri}")
    private lateinit var redirectUri: String

    @Value("\${login.provider.oauth2.microsoft.scopes}")
    private lateinit var scopes: Array<String>

    @Test
    fun startMicrosoftAuthenticationProvide200Ok() {
        webTestClient.get()
            .uri(START_URI)
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun startMicrosoftAuthenticationProvideValidHeaderContainsRedirectToLogin() {
        webTestClient.get()
            .uri(START_URI)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(MicrosoftOAuthResponseDtoV1::class.java)
            .consumeWith {
                val body = it.responseBody
                Assertions.assertNotNull(body)
                Assertions.assertEquals(
                    body!!.redirectURI, UriComponentsBuilder.fromHttpUrl(authenticationEndpoint)
                        .queryParam("client_id", clientId)
                        .queryParam("response_type", responseType)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("scope", scopes.joinToString(" "))
                        .buildAndExpand()
                        .toUri()
                        .toString()
                )
            }
    }

    @Test
    fun finishMicrosoftAuthenticationProvide200Ok() {
        stubExternalApi(AUTH_RESPONSE, ME_RESPONSE)

        webTestClient.get()
            .uri { it.path(FINISH_URI).queryParam("code", UUID.randomUUID().toString()).build() }
            .attribute("code", UUID.randomUUID().toString())
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun finishMicrosoftAuthenticationProvideValidJwt() {
        stubExternalApi(AUTH_RESPONSE, ME_RESPONSE)

        webTestClient.get()
            .uri { it.path(FINISH_URI).queryParam("code", UUID.randomUUID().toString()).build() }
            .attribute("code", UUID.randomUUID().toString())
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(AuthenticationResponseDtoV1::class.java)
            .consumeWith {
                Assertions.assertEquals(jwtUtil.parseUsernameFromToken(it.responseBody!!.token), DISPLAY_NAME)
            }
    }

    @Test
    fun finishMicrosoftAuthenticationWithNonUniversityEmailProvide406NotAcceptable() {
        val notUniversityEmailResponse = MicrosoftMeResponseDtoV1(
            displayName = DISPLAY_NAME,
            mail = "$DISPLAY_NAME@${UUID.randomUUID()}.com",
            userPrincipalName = "$DISPLAY_NAME@${UUID.randomUUID()}.com"
        )
        stubExternalApi(AUTH_RESPONSE, notUniversityEmailResponse)

        webTestClient.get()
            .uri { it.path(FINISH_URI).queryParam("code", UUID.randomUUID().toString()).build() }
            .attribute("code", UUID.randomUUID().toString())
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.NOT_ACCEPTABLE)
    }

    @Test
    fun finishMicrosoftAuthenticationWithNullEmailProvide406NotAcceptable() {
        val notUniversityEmailResponse = MicrosoftMeResponseDtoV1(
            displayName = DISPLAY_NAME,
            userPrincipalName = "$DISPLAY_NAME@test.com"
        )
        stubExternalApi(AUTH_RESPONSE, notUniversityEmailResponse)

        webTestClient.get()
            .uri { it.path(FINISH_URI).queryParam("code", UUID.randomUUID().toString()).build() }
            .attribute("code", UUID.randomUUID().toString())
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.NOT_ACCEPTABLE)
    }

    @Test
    fun finishMicrosoftAuthenticationWithEmptyEmailProvide406NotAcceptable() {
        val notUniversityEmailResponse = MicrosoftMeResponseDtoV1(
            displayName = DISPLAY_NAME,
            mail = "",
            userPrincipalName = "$DISPLAY_NAME@test.com"
        )
        stubExternalApi(AUTH_RESPONSE, notUniversityEmailResponse)

        webTestClient.get()
            .uri { it.path(FINISH_URI).queryParam("code", UUID.randomUUID().toString()).build() }
            .attribute("code", UUID.randomUUID().toString())
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.NOT_ACCEPTABLE)
    }

    @Test
    fun finishMicrosoftAuthenticationWithNullUserPrincipalNameProvide406NotAcceptable() {
        val notUniversityEmailResponse = MicrosoftMeResponseDtoV1(
            displayName = DISPLAY_NAME,
            mail = "$DISPLAY_NAME@test.com"
        )
        stubExternalApi(AUTH_RESPONSE, notUniversityEmailResponse)

        webTestClient.get()
            .uri { it.path(FINISH_URI).queryParam("code", UUID.randomUUID().toString()).build() }
            .attribute("code", UUID.randomUUID().toString())
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.NOT_ACCEPTABLE)
    }

    @Test
    fun finishMicrosoftAuthenticationWithEmptyUserPrincipalNameProvide406NotAcceptable() {
        val notUniversityEmailResponse = MicrosoftMeResponseDtoV1(
            displayName = DISPLAY_NAME,
            mail = "$DISPLAY_NAME@test.com",
            userPrincipalName = ""
        )
        stubExternalApi(AUTH_RESPONSE, notUniversityEmailResponse)

        webTestClient.get()
            .uri { it.path(FINISH_URI).queryParam("code", UUID.randomUUID().toString()).build() }
            .attribute("code", UUID.randomUUID().toString())
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.NOT_ACCEPTABLE)
    }

    @Test
    fun finishMicrosoftAuthenticationWithNullDisplayNameProvide406NotAcceptable() {
        val notUniversityEmailResponse = MicrosoftMeResponseDtoV1(
            mail = "$DISPLAY_NAME@test.com",
            userPrincipalName = "$DISPLAY_NAME@test.com"
        )
        stubExternalApi(AUTH_RESPONSE, notUniversityEmailResponse)

        webTestClient.get()
            .uri { it.path(FINISH_URI).queryParam("code", UUID.randomUUID().toString()).build() }
            .attribute("code", UUID.randomUUID().toString())
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.NOT_ACCEPTABLE)
    }

    @Test
    fun finishMicrosoftAuthenticationWithEmptyDisplayNameProvide406NotAcceptable() {
        val notUniversityEmailResponse = MicrosoftMeResponseDtoV1(
            displayName = "",
            mail = "$DISPLAY_NAME@test.com",
            userPrincipalName = "$DISPLAY_NAME@test.com"
        )
        stubExternalApi(AUTH_RESPONSE, notUniversityEmailResponse)

        webTestClient.get()
            .uri { it.path(FINISH_URI).queryParam("code", UUID.randomUUID().toString()).build() }
            .attribute("code", UUID.randomUUID().toString())
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.NOT_ACCEPTABLE)
    }

    @Test
    fun finishMicrosoftAuthenticationWithNoAccessTokenProvide503ServiceUnavailable() {
        stubExternalApi(MicrosoftAuthTokenResponseDtoV1(), ME_RESPONSE)

        webTestClient.get()
            .uri { it.path(FINISH_URI).queryParam("code", UUID.randomUUID().toString()).build() }
            .attribute("code", UUID.randomUUID().toString())
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
    }

    private fun stubExternalApi(authResponse: MicrosoftAuthTokenResponseDtoV1, meResponse: MicrosoftMeResponseDtoV1) {
        stubFor(
            post(urlEqualTo(MICROSOFT_TOKEN_URI))
                .willReturn(
                    aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(authResponse))
                )
        )

        stubFor(
            get(urlEqualTo(MICROSOFT_ME_URI))
                .willReturn(
                    aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(meResponse))
                )
        )
    }

    private companion object {
        const val START_URI: String = "/v1/authentication/microsoft/start"
        const val FINISH_URI: String = "/v1/authentication/microsoft/finish"

        const val MICROSOFT_TOKEN_URI = "/common/oauth2/v2.0/token"
        const val MICROSOFT_ME_URI = "/v1.0/me"

        const val DISPLAY_NAME = "test"
        const val MAIL = "$DISPLAY_NAME@test.com"
        const val USER_PRINCIPAL_NAME = MAIL

        val AUTH_RESPONSE = MicrosoftAuthTokenResponseDtoV1(accessToken = UUID.randomUUID().toString())
        val ME_RESPONSE =
            MicrosoftMeResponseDtoV1(displayName = DISPLAY_NAME, mail = MAIL, userPrincipalName = USER_PRINCIPAL_NAME)
    }
}