package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_PAGE
import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_PAGE_SIZE
import cz.fei.upce.checkman.domain.challenge.solution.Solution
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.review.Review
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceImpl
import cz.fei.upce.checkman.service.course.challenge.solution.FeedbackService
import cz.fei.upce.checkman.service.course.challenge.solution.ReviewService
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.upce.fei.checkman.domain.course.security.annotation.ChallengeId
import cz.upce.fei.checkman.domain.course.security.annotation.CourseId
import cz.upce.fei.checkman.domain.course.security.annotation.ReviewId
import cz.upce.fei.checkman.domain.course.security.annotation.SolutionId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
@Validated
class ReviewController(
    private val reviewService: ReviewService,
    private val feedbackService: FeedbackService,
    private val authenticationService: AuthenticationServiceImpl
) {
    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun countToReview(@ChallengeId @Argument challengeId: Long, authentication: Authentication): Mono<Int> {
        return reviewService.countToReview(challengeId).map { it.toInt() }
    }

    @QueryMapping
    fun allSolutionsToReview(@CourseId @Argument courseId: Long, authentication: Authentication): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.CoursesReviewListQL> {
        return reviewService.findAllToReview(courseId, authenticationService.extractAuthenticateUser(authentication))
    }

    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun solutionsToReview(@ChallengeId @Argument challengeId: Long?,
                          @Argument page: Int = DEFAULT_PAGE,
                          @Argument pageSize: Int = DEFAULT_PAGE_SIZE,
                          authentication: Authentication): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.SolutionQL> {
        return reviewService.findAllToReview(challengeId, page, pageSize)
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun createReview(@SolutionId @Argument solutionId: Long, @Argument reviewInput: cz.fei.upce.checkman.dto.graphql.input.course.challenge.ReviewInputQL, authentication: Authentication): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.ReviewQL> {
        return reviewService.create(solutionId, reviewInput, authenticationService.extractAuthenticateUser(authentication))
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun publishReview(@ReviewId @Argument id: Long, @Argument status: Solution.Status = Solution.Status.APPROVED, authentication: Authentication): Mono<Boolean> {
        return reviewService.publish(id, status)
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun editReview(@ReviewId @Argument id: Long, @Argument input: cz.fei.upce.checkman.dto.graphql.input.course.challenge.ReviewInputQL, authentication: Authentication): Mono<Review> {
        return reviewService.edit(id, input)
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun deleteReview(@ReviewId @Argument reviewId: Long, authentication: Authentication) {

    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun removeFeedbackFromReview(@ReviewId @Argument reviewId: Long, @Argument feedbackId: Long, authentication: Authentication): Mono<Boolean> {
        return reviewService.unlinkFeedback(reviewId, feedbackId)
            .map { true }
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun addFeedbackToReview(@ReviewId @Argument reviewId: Long, @Argument feedbackId: Long, authentication: Authentication): Mono<Boolean> {
        return reviewService.linkFeedback(reviewId, feedbackId)
            .map { true }
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun createFeedbackToReview(@ReviewId @Argument reviewId: Long, @Argument feedback: cz.fei.upce.checkman.dto.graphql.input.course.challenge.solution.FeedbackInputQL, authentication: Authentication): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.FeedbackQL> {
        return feedbackService.create(feedback)
            .flatMap { createFeedback ->
                reviewService.linkFeedback(reviewId, createFeedback.id!!)
                .map { createFeedback.toQL() }
                .switchIfEmpty(Mono.just(createFeedback.toQL()))
            }
    }

    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.VIEW_REVIEW])
    fun review(@Argument @ReviewId id: Long, authentication: Authentication) : Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.ReviewQL> {
        return reviewService.findByIdAsQL(id)
    }

    @SchemaMapping(typeName = "Solution", field = "review")
    fun reviewBySolution(solution: cz.fei.upce.checkman.dto.graphql.output.challenge.solution.SolutionQL): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.ReviewQL> {
        return reviewService.findBySolutionIdAsQL(solution.id!!)
    }
}