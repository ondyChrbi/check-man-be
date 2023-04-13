package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.graphql.input.course.challenge.ChallengeInputQL
import cz.fei.upce.checkman.graphql.output.challenge.ChallengeQL
import cz.fei.upce.checkman.graphql.output.challenge.PermittedAppUserChallengeQL
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import cz.fei.upce.checkman.service.course.challenge.ChallengeServiceV1
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
import reactor.core.publisher.Mono
import javax.validation.Valid

@Controller
@Validated
class ChallengeQLController(
    private val challengeService: ChallengeServiceV1,
    private val authenticationService: AuthenticationServiceV1
) {
    @QueryMapping
    @PreCourseSemesterAuthorize
    fun challenges(@SemesterId @Argument semesterId: Long, authentication: Authentication) =
        challengeService.findAllBySemesterIdAsQL(semesterId, authenticationService.extractAuthenticateUser(authentication))

    @QueryMapping
    @PreCourseSemesterAuthorize
    fun challenge(@ChallengeId @Argument id: Long, authentication: Authentication) =
        challengeService.findByIdAsQL(id)

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.CREATE_CHALLENGE])
    fun createChallenge(
        @SemesterId @Argument semesterId: Long,
        @Argument @Valid input: ChallengeInputQL,
        authentication: Authentication
    ) = challengeService.addAsQL(semesterId, input, authenticationService.extractAuthenticateUser(authentication))

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun editChallenge(
        @ChallengeId @Argument challengeId: Long,
        @Argument input: ChallengeInputQL,
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