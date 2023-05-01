package cz.fei.upce.checkman.service.course.challenge.solution

import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_OFFSET
import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_SIZE
import cz.fei.upce.checkman.domain.challenge.solution.Solution
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.review.Review
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.graphql.input.course.challenge.ReviewInputQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.*
import cz.fei.upce.checkman.repository.review.ReviewRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import cz.fei.upce.checkman.service.appuser.AppUserService
import cz.fei.upce.checkman.service.course.challenge.ChallengeService
import cz.fei.upce.checkman.service.course.security.CourseAuthorizationService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ReviewService(
    private val solutionService: SolutionService,
    private val appUserService: AppUserService,
    private val authorizationService: CourseAuthorizationService,
    private val challengeService: ChallengeService,
    private val reviewRepository: ReviewRepository
) {
    fun countToReview(challengeId: Long?): Mono<Long> {
        return solutionService.countToReview(challengeId!!)
    }

    fun findAllToReview(challengeId: Long?, offset: Int = DEFAULT_OFFSET, size: Int = DEFAULT_SIZE): Flux<SolutionQL> {
        return solutionService.findAllToReview(challengeId!!, offset, size)
            .flatMap { solution ->
                val review = solutionService.findReviewAsQL(solution.id!!)

                appUserService.findById(solution.userId)
                    .flatMap { appUser ->
                        review.map { review ->
                            solution.toQL(review, appUser)
                        }.switchIfEmpty(Mono.just(solution.toQL(author = appUser)))
                    }.switchIfEmpty(Mono.just(solution.toQL()))
            }
    }

    fun findAllToReview(courseId: Long, reviewer: AppUser): Flux<CoursesReviewListQL> {
        val courses = authorizationService.findAllCoursesWhereUserHasRoles(
            courseId,
            reviewer,
            listOf(CourseSemesterRole.Value.REVIEW_CHALLENGE.id)
        )

        return courses.flatMap { courseSemester ->
            challengeService.findAllBySemesterIdAsQL(
                courseSemester.id!!,
                reviewer
            )
                .flatMap { challenge ->
                    findAllToReview(challenge.id!!)
                        .collectList()
                        .map { solutions ->
                            ChallengeSolutionsQL(challenge, solutions)
                        }
                }
                .collectList()
                .map { CoursesReviewListQL(courseSemester.toQL(), it) }
        }
    }

    fun unlinkFeedback(reviewId: Long, feedbackId: Long): Mono<Void> {
        return reviewRepository.unlinkFeedback(reviewId, feedbackId)
    }

    fun linkFeedback(reviewId: Long, feedbackId: Long): Mono<Void> {
        return reviewRepository.linkFeedback(reviewId, feedbackId)
    }

    fun create(solutionId: Long, reviewInput: ReviewInputQL, author: AppUser): Mono<ReviewQL> {
        return reviewRepository.save(reviewInput.toEntity(solutionId, author.id!!))
            .map { it.toQL() }
    }

    fun publish(reviewId: Long, status: Solution.Status = Solution.Status.APPROVED): Mono<Boolean> {
        val solution = solutionService.updateStatus(reviewId, status)

        return solution.flatMap {
            reviewRepository.publish(reviewId)
                .switchIfEmpty(Mono.error(ResourceNotFoundException()))
                .collectList()
                .map { it.size > 0 }
        }
    }

    fun edit(id: Long, reviewInput: ReviewInputQL): Mono<Review> {
        return reviewRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { reviewRepository.save(it.update(reviewInput)) }
    }

    fun findByIdAsQL(id: Long): Mono<ReviewQL> {
        return reviewRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { it.toQL() }
    }

    fun findBySolutionIdAsQL(solutionId: Long): Mono<ReviewQL> {
        return reviewRepository.findFirstBySolutionIdEquals(solutionId)
            .map { it.toQL() }
    }
}