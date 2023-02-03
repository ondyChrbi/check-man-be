package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.graphql.output.challenge.solution.CoursesReviewListQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.SolutionQL
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import cz.fei.upce.checkman.service.course.challenge.solution.ReviewServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.ChallengeId
import cz.fei.upce.checkman.service.course.security.annotation.CourseId
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.fei.upce.checkman.service.course.security.annotation.ReviewId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
@Validated
class ReviewQLController(
    private val reviewService: ReviewServiceV1,
    private val authenticationService: AuthenticationServiceV1
) {
    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun countToReview(@ChallengeId @Argument challengeId: Long, authentication: Authentication): Mono<Int> {
        return reviewService.countToReview(challengeId).map { it.toInt() }
    }

    @QueryMapping
    fun allSolutionsToReview(@CourseId @Argument courseId: Long, authentication: Authentication): Flux<CoursesReviewListQL> {
        return reviewService.findAllToReview(courseId, authenticationService.extractAuthenticateUser(authentication))
    }

    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun solutionsToReview(@ChallengeId @Argument challengeId: Long?, authentication: Authentication): Flux<SolutionQL> {
        return reviewService.findAllToReview(challengeId)
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun createReview(@ChallengeId @Argument challengeId: Long, solutionId: Long, authentication: Authentication) {

    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun editReview(@ReviewId reviewId: Long, @Argument solutionId: Long, authentication: Authentication) {

    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun deleteReview(@ReviewId @Argument reviewId: Long, authentication: Authentication) {

    }
}