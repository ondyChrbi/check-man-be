package cz.fei.upce.checkman.service

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.repository.user.AppUserRepository
import cz.fei.upce.checkman.repository.user.AppUserTeamRepository
import cz.fei.upce.checkman.repository.user.TeamRepository
import cz.fei.upce.checkman.service.appuser.AppUserServiceV1
import cz.fei.upce.checkman.service.appuser.TeamServiceV1
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
internal class AppUserServiceV1Test {
    @Value("check-man.team.private.post-fix")
    private var privateTeamPostFix = "private"

    @Autowired
    private lateinit var appUserService: AppUserServiceV1

    @Autowired
    private lateinit var appUserRepository: AppUserRepository

    @Autowired
    private lateinit var teamRepository: TeamRepository

    @Autowired
    private lateinit var appUserTeamRepository: AppUserTeamRepository

    private lateinit var testAppUser: AppUser

    @BeforeEach
    internal fun setUp() {
        testAppUser = AppUser(
            stagId = "st55551",
            mail = "test@test.com",
            displayName = "test",
            registrationDate = LocalDateTime.now(),
            lastAccessDate = LocalDateTime.now(),
            disabled = false
        )
    }

    @AfterEach
    fun tearDown() {
        appUserTeamRepository.deleteAll().block()
        teamRepository.deleteAll().block()
        appUserRepository.deleteAll().block()
    }

    @Test
    fun saveAppUserWillProvideHisNewId() {
        StepVerifier.create(appUserService.save(testAppUser))
            .expectNextMatches { it.id != null }
            .verifyComplete()
    }

    @Test
    fun saveAppUserWillCreateOnePrivateTeam() {
        val user = appUserService.save(testAppUser).block()

        StepVerifier.create(teamRepository.findPersonalTeam(user!!.id!!))
            .expectNextCount(TeamServiceV1.PRIVATE_TEAM_MIN_MEMBERS.toLong())
            .verifyComplete()
    }

    @Test
    fun saveAppUserWillCreatePrivateTeamWithCorrectTeamName() {
        val user = appUserService.save(testAppUser).block()

        StepVerifier.create(teamRepository.findPersonalTeam(user!!.id!!))
            .expectNextMatches {
                it.name == TeamServiceV1.createPrivateTeamName(
                    testAppUser.stagId!!,
                    privateTeamPostFix
                )
            }
            .verifyComplete()
    }
}