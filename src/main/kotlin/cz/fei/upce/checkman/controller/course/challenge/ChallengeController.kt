package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.dto.graphql.output.challenge.ChallengeQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.PermittedAppUserChallengeQL
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceImpl
import cz.fei.upce.checkman.service.course.challenge.ChallengeService
import cz.upce.fei.checkman.domain.course.security.annotation.ChallengeId
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.upce.fei.checkman.domain.course.security.annotation.SemesterId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.Valid

@Controller
@Validated
class ChallengeController(
    private val challengeService: ChallengeService,
    private val authenticationService: AuthenticationServiceImpl
) {
    @QueryMapping
    @PreCourseSemesterAuthorize
    fun challenges(
        @SemesterId @Argument semesterId: Long,
        @Argument pageSize: Int? = CheckManApplication.DEFAULT_PAGE_SIZE,
        @Argument page: Int? = CheckManApplication.DEFAULT_PAGE,
        authentication: Authentication
    ): Flux<ChallengeQL> {
        return challengeService.findAllBySemesterIdAsQL(semesterId, authenticationService.extractAuthenticateUser(authentication))
    }

    @QueryMapping
    @PreCourseSemesterAuthorize
    fun challenge(@ChallengeId @Argument id: Long, authentication: Authentication): Mono<ChallengeQL> {
        val appUser = authenticationService.extractAuthenticateUser(authentication)

        return challengeService.findByIdAsQL(id, appUser)
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.CREATE_CHALLENGE])
    fun createChallenge(
        @SemesterId @Argument semesterId: Long,
        @Argument @Valid input: cz.fei.upce.checkman.dto.graphql.input.course.challenge.ChallengeInputQL,
        authentication: Authentication
    ) = challengeService.addAsQL(semesterId, input, authenticationService.extractAuthenticateUser(authentication))

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun editChallenge(
        @ChallengeId @Argument challengeId: Long,
        @Argument input: cz.fei.upce.checkman.dto.graphql.input.course.challenge.ChallengeInputQL,
        authentication: Authentication
    ) = challengeService.editAsQL(challengeId, input, authenticationService.extractAuthenticateUser(authentication))

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.DELETE_CHALLENGE])
    fun deleteChallenge(
        @ChallengeId @Argument challengeId: Long,
        authentication: Authentication
    ): Mono<ChallengeQL> {
        return challengeService.deleteAsQL(challengeId, authenticationService.extractAuthenticateUser(authentication))
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun publishChallenge(@ChallengeId @Argument challengeId: Long,
                         authentication: Authentication) : Mono<Boolean> {
        return challengeService.publish(challengeId, authenticationService.extractAuthenticateUser(authentication))
    }

    @SchemaMapping(typeName = "PermittedAppUserChallenge", field = "challenge")
    fun challenge(permittedAppUserChallenge: PermittedAppUserChallengeQL): Mono<ChallengeQL> {
        return challengeService.findByPermittedAppUserChallengeIdAsQL(permittedAppUserChallenge.id!!)
    }
}