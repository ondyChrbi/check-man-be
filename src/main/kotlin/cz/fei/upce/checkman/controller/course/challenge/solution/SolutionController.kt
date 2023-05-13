package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.service.authentication.AuthenticationServiceImpl
import cz.fei.upce.checkman.service.course.challenge.solution.SolutionService
import cz.upce.fei.checkman.domain.course.security.annotation.ChallengeId
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.upce.fei.checkman.domain.course.security.annotation.SolutionId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
@Validated
class SolutionController(
    private val solutionService: SolutionService,
    private val authenticationService: AuthenticationServiceImpl,
    ) {
    @QueryMapping
    @PreCourseSemesterAuthorize
    fun solutions(@ChallengeId @Argument challengeId: Long,
                  authentication: Authentication): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.SolutionQL> {
        return solutionService.findAllByChallengeAndUser(challengeId, authenticationService.extractAuthenticateUser(authentication))
    }

    @QueryMapping
    @PreCourseSemesterAuthorize
    fun solution(@SolutionId @Argument id: Long, authentication: Authentication): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.SolutionQL> {
        return solutionService.findById(id, authenticationService.extractAuthenticateUser(authentication))
    }
}