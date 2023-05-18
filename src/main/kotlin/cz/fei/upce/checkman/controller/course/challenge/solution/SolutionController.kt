package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.SolutionQL
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
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun solutions(
        @ChallengeId @Argument challengeId: Long,
        @Argument pageSize: Int? = CheckManApplication.DEFAULT_LIMIT,
        @Argument page: Int? = CheckManApplication.DEFAULT_OFFSET,
        authentication: Authentication
    ): Flux<SolutionQL> {

        return solutionService.findAllByChallenge(challengeId, page, pageSize)
            .map { it.toQL() }
    }
    
    @QueryMapping
    @PreCourseSemesterAuthorize
    fun mySolutions(@ChallengeId @Argument challengeId: Long,
                  authentication: Authentication): Flux<SolutionQL> {
        return solutionService.findAllByChallengeAndUser(challengeId, authenticationService.extractAuthenticateUser(authentication))
    }

    @QueryMapping
    @PreCourseSemesterAuthorize
    fun solution(@SolutionId @Argument id: Long, authentication: Authentication): Mono<SolutionQL> {
        return solutionService.findById(id, authenticationService.extractAuthenticateUser(authentication))
    }

}