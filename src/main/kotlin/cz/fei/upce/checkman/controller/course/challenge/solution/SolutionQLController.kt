package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.graphql.output.challenge.solution.SolutionQL
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import cz.fei.upce.checkman.service.course.challenge.solution.SolutionServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.ChallengeId
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.fei.upce.checkman.service.course.security.annotation.SolutionId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
@Validated
class SolutionQLController(
    private val solutionService: SolutionServiceV1,
    private val authenticationService: AuthenticationServiceV1,
    ) {
    @QueryMapping
    @PreCourseSemesterAuthorize
    fun solutions(@ChallengeId @Argument challengeId: Long,
                  authentication: Authentication): Flux<SolutionQL> {
        return solutionService.findAllByChallengeAndUser(challengeId, authenticationService.extractAuthenticateUser(authentication))
    }

    @QueryMapping
    @PreCourseSemesterAuthorize
    fun solution(@SolutionId @Argument id: Long, authentication: Authentication): Mono<SolutionQL> {
        return solutionService.findById(id, authenticationService.extractAuthenticateUser(authentication))
    }
}