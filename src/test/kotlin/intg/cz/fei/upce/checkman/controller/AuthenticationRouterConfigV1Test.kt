package cz.fei.upce.checkman.controller

import cz.fei.upce.checkman.component.security.JWTUtil
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.security.AuthenticationRequestDtoV1
import cz.fei.upce.checkman.dto.security.AuthenticationResponseDtoV1
import cz.fei.upce.checkman.repository.user.AppUserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
internal class AuthenticationRouterConfigV1Test {
    @Autowired
    private lateinit var webTestClient : WebTestClient

    @Autowired
    private lateinit var jwtUtil: JWTUtil

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @Autowired
    private lateinit var databaseTemplate : R2dbcEntityTemplate

    @AfterEach
    internal fun tearDown() {
        appUserRepository.deleteAll().block()
    }

    @Test
    fun loginWithExistingUserProvide200SuccessAndBody() {
        databaseTemplate.insert(APP_USER).block()

        webTestClient.post()
            .uri(LOGIN_URI)
            .bodyValue(AuthenticationRequestDtoV1(APP_USER.stagId!!))
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun loginWithExistingUserProvideAuthenticationResponseBody() {
        databaseTemplate.insert(APP_USER).block()

        webTestClient.post()
            .uri(LOGIN_URI)
            .bodyValue(AuthenticationRequestDtoV1(APP_USER.stagId!!))
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(AuthenticationResponseDtoV1::class.java)
    }

    @Test
    fun loginWithExistingUserProvideJwtTokenForTheUser() {
        databaseTemplate.insert(APP_USER).block()

        webTestClient.post()
            .uri(LOGIN_URI)
            .bodyValue(AuthenticationRequestDtoV1(APP_USER.stagId!!))
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(AuthenticationResponseDtoV1::class.java)
            .consumeWith {
                assertEquals(jwtUtil.parseUsernameFromToken(it.responseBody!!.token), APP_USER.stagId!!)
            }
    }

    @Test
    fun loginWithExistingUserProvideNotExpiredJwtToken() {
        databaseTemplate.insert(APP_USER).block()

        webTestClient.post()
            .uri(LOGIN_URI)
            .bodyValue(AuthenticationRequestDtoV1(APP_USER.stagId!!))
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(AuthenticationResponseDtoV1::class.java)
            .consumeWith {
                assertFalse(jwtUtil.isExpired(it.responseBody!!.token))
            }
    }

    @Test
    fun loginWithNonExistingUserProvide401Unauthorized() {
         webTestClient.post()
            .uri(LOGIN_URI)
            .bodyValue(AuthenticationRequestDtoV1(APP_USER.stagId!!))
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun loginWithNonExistingUserProvideNoJwtTokenResponseBody() {
        webTestClient.post()
            .uri(LOGIN_URI)
            .bodyValue(AuthenticationRequestDtoV1(APP_USER.stagId!!))
            .exchange()
            .expectStatus()
            .isUnauthorized
            .expectBody(String::class.java)
    }



    private companion object {
        const val LOGIN_URI : String = "/v1/authentication/login"
        val APP_USER = AppUser(1, "st00001", LocalDateTime.now(), LocalDateTime.now(), false)
    }
}