package cz.fei.upce.checkman.controller

import cz.fei.upce.checkman.dto.course.CourseDtoV1
import cz.fei.upce.checkman.repository.course.CourseRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.util.UUID

@AutoConfigureWireMock(port = 8084)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class CourseControllerV1Test {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll().block()
    }

    @AfterEach
    internal fun tearDown() {
        courseRepository.deleteAll().block()
    }

    @Test
    fun addNewCourseWithoutAuthenticationWillProvide401Unauthorized() {
        webTestClient.post()
            .uri(ADD_COURSE_URI)
            .body(Mono.just(NEW_COURSE), CourseDtoV1::class.java)
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    private companion object {
        const val ADD_COURSE_URI: String = "/v1/course"

        val NEW_COURSE = CourseDtoV1(
            stagId = UUID.randomUUID().toString(),
            name = UUID.randomUUID().toString()
        )
    }
}