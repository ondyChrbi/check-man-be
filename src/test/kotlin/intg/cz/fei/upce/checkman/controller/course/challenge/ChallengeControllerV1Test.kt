package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.component.security.JWTUtil
import cz.fei.upce.checkman.domain.course.AppUserCourseSemesterRole
import cz.fei.upce.checkman.domain.course.Course
import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.domain.user.AppUserGlobalRole
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_CHALLENGE_ACCESS
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_COURSE_MANAGE
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_COURSE_VIEW
import cz.fei.upce.checkman.repository.course.AppUserCourseSemesterRoleRepository
import cz.fei.upce.checkman.repository.course.CourseRepository
import cz.fei.upce.checkman.repository.course.CourseSemesterRepository
import cz.fei.upce.checkman.repository.user.AppUserGlobalRoleRepository
import cz.fei.upce.checkman.repository.user.AppUserRepository
import cz.fei.upce.checkman.repository.user.GlobalRoleRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

@AutoConfigureWireMock(port = 8084)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class ChallengeControllerV1Test {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var jwtUtil: JWTUtil

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @Autowired
    private lateinit var courseSemesterRepository: CourseSemesterRepository

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @Autowired
    private lateinit var appUserCourseSemesterRoleRepository: AppUserCourseSemesterRoleRepository

    @Autowired
    private lateinit var globalRoleRepository: GlobalRoleRepository

    @Autowired
    private lateinit var appUserGlobalRoleRepository: AppUserGlobalRoleRepository

    private lateinit var appUser: AppUser

    private lateinit var course: Course

    private lateinit var courseSemester: CourseSemester

    @BeforeEach
    fun setUp() {
        appUser = appUserRepository.save(AppUser(
            stagId = "st00000",
            mail = "st00000@upce.cz",
            displayName = "Test Test",
            registrationDate = LocalDateTime.now(),
            lastAccessDate = LocalDateTime.now(),
            disabled = false
        )).block()!!

        course = courseRepository.save(Course(
            stagId = "KTS/BTEST",
            name = "Testovaci scenar",
            dateCreation = LocalDateTime.now(),
            icon = "test.jpg",
            template = "#00000"
        )).block()!!

        courseSemester = courseSemesterRepository.save(CourseSemester(
            note = "Test",
            dateStart = LocalDateTime.now(),
            dateEnd = LocalDateTime.now(),
            courseId = course.id
        )).block()!!
    }

    @AfterEach
    fun tearDown() {
        courseSemesterRepository.deleteAll().block()
        courseRepository.deleteAll().block()
        appUserGlobalRoleRepository.deleteAll().block()
        appUserRepository.deleteAll().block()
    }

    @Test
    fun noGlobalRoleAndNoCourseRoleWillCause403ForbiddenAccess() {
        webTestClient.get()
            .uri(toSearchUrl(course.id!!, courseSemester.id!!))
            .header("Authorization", "Bearer ${jwtUtil.generateToken(appUser.stagId)}")
            .exchange()
            .expectStatus()
            .isForbidden
    }

    @Test
    fun viewGlobalRoleCourseViewProvide200OkAccess() {
        val role = globalRoleRepository.findFirstByNameEquals(ROLE_COURSE_VIEW).block()
        appUserGlobalRoleRepository.save(AppUserGlobalRole(appUserId = appUser.id, globalRoleId = role!!.id)).block()

        webTestClient.get()
            .uri(toSearchUrl(course.id!!, courseSemester.id!!))
            .header("Authorization", "Bearer ${jwtUtil.generateToken(this.appUser.stagId)}")
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun viewGlobalRoleCourseManageProvide200OkAccess() {
        val role = globalRoleRepository.findFirstByNameEquals(ROLE_COURSE_MANAGE).block()
        appUserGlobalRoleRepository.save(AppUserGlobalRole(appUserId = appUser.id, globalRoleId = role!!.id)).block()

        webTestClient.get()
            .uri(toSearchUrl(course.id!!, courseSemester.id!!))
            .header("Authorization", "Bearer ${jwtUtil.generateToken(this.appUser.stagId)}")
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun viewWithAnyOtherGlobalRoleExpectPermittedCause403ForbiddenAccess() {
        val permitted = listOf(ROLE_COURSE_VIEW, ROLE_COURSE_MANAGE, ROLE_CHALLENGE_ACCESS)

        val bearerToken = jwtUtil.generateToken(appUser.stagId)
        val globalRoles = globalRoleRepository.findAll().collectList().block()

        globalRoles!!.filter { !permitted.contains(it.name) }.forEach { globalRole ->
            appUserGlobalRoleRepository.deleteAll().block()
            appUserGlobalRoleRepository.save(AppUserGlobalRole(appUserId = appUser.id, globalRoleId = globalRole!!.id)).block()

            webTestClient.get()
                .uri(toSearchUrl(course.id!!, courseSemester.id!!))
                .header("Authorization", "Bearer $bearerToken")
                .exchange()
                .expectStatus()
                .isForbidden
        }
    }

    @Test
    fun viewGlobalRoleChallengeAccessWithoutTakingPartInCourseCause403ForbiddenAccess() {
        val role = globalRoleRepository.findFirstByNameEquals(ROLE_CHALLENGE_ACCESS).block()
        appUserGlobalRoleRepository.save(AppUserGlobalRole(appUserId = appUser.id, globalRoleId = role!!.id)).block()

        webTestClient.get()
            .uri(toSearchUrl(course.id!!, courseSemester.id!!))
            .header("Authorization", "Bearer ${jwtUtil.generateToken(this.appUser.stagId)}")
            .exchange()
            .expectStatus()
            .isForbidden
    }

    @Test
    fun viewGlobalRoleChallengeAccessWithTakingPartInCourseCause200OkAccess() {
        val globalRole = globalRoleRepository.findFirstByNameEquals(ROLE_CHALLENGE_ACCESS).block()

        appUserGlobalRoleRepository.save(AppUserGlobalRole(appUserId = appUser.id, globalRoleId = globalRole!!.id))
            .block()
        appUserCourseSemesterRoleRepository.save(AppUserCourseSemesterRole(
            appUserId = appUser.id!!,
            courseSemesterRoleId = CourseSemesterRole.Value.ACCESS.id,
            courseSemesterId = courseSemester.id!!
        )).block()

        webTestClient.get()
            .uri(toSearchUrl(course.id!!, courseSemester.id!!))
            .header("Authorization", "Bearer ${jwtUtil.generateToken(this.appUser.stagId)}")
            .exchange()
            .expectStatus()
            .isOk
    }

    private companion object {
        fun toSearchUrl(courseId: Long, semesterId: Long) = "/v1/course/$courseId/semester/$semesterId/challenge"
    }
}