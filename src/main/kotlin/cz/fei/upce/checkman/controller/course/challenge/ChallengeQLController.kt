package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.graphql.input.course.ChallengeInputQL
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import cz.fei.upce.checkman.service.course.challenge.ChallengeServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.ChallengeId
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.fei.upce.checkman.service.course.security.annotation.SemesterId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
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
        challengeService.findAllBySemesterIdAsQL(semesterId)

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
}