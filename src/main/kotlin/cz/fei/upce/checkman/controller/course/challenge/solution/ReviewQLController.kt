package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_OFFSET
import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_SIZE
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.review.Review
import cz.fei.upce.checkman.graphql.input.course.challenge.ReviewInputQL
import cz.fei.upce.checkman.graphql.input.course.challenge.solution.FeedbackInputQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.CoursesReviewListQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.FeedbackQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.ReviewQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.SolutionQL
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import cz.fei.upce.checkman.service.course.challenge.solution.FeedbackServiceV1
import cz.fei.upce.checkman.service.course.challenge.solution.ReviewServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.*
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
    private val feedbackService: FeedbackServiceV1,
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
    fun solutionsToReview(@ChallengeId @Argument challengeId: Long?,
                          @Argument offset: Int = DEFAULT_OFFSET,
                          @Argument size: Int = DEFAULT_SIZE,
                          authentication: Authentication): Flux<SolutionQL> {
        return reviewService.findAllToReview(challengeId, offset, size)
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun createReview(@SolutionId @Argument solutionId: Long, @Argument reviewInput: ReviewInputQL, authentication: Authentication): Mono<ReviewQL> {
        return reviewService.create(solutionId, reviewInput, authenticationService.extractAuthenticateUser(authentication))
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun publishReview(@ReviewId @Argument id: Long, authentication: Authentication): Mono<Boolean> {
        return reviewService.publish(id)
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun editReview(@ReviewId @Argument id: Long, @Argument input: ReviewInputQL, authentication: Authentication): Mono<Review> {
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
    fun createFeedbackToReview(@ReviewId @Argument reviewId: Long, @Argument feedback: FeedbackInputQL,  authentication: Authentication): Mono<FeedbackQL> {
        return feedbackService.create(feedback)
            .flatMap { createFeedback ->
                reviewService.linkFeedback(reviewId, createFeedback.id!!)
                .map { createFeedback.toQL() }
                .switchIfEmpty(Mono.just(createFeedback.toQL()))
            }
    }
}