package cz.fei.upce.checkman.service.course.challenge.solution

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.graphql.output.challenge.solution.*
import cz.fei.upce.checkman.service.appuser.AppUserServiceV1
import cz.fei.upce.checkman.service.course.challenge.ChallengeServiceV1
import cz.fei.upce.checkman.service.course.security.CourseAuthorizationServiceV1
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ReviewServiceV1(
    private val solutionService: SolutionServiceV1,
    private val appUserService: AppUserServiceV1,
    private val authorizationService: CourseAuthorizationServiceV1,
    private val challengeService: ChallengeServiceV1,
) {
    fun countToReview(challengeId: Long?): Mono<Long> {
        return solutionService.countToReview(challengeId!!)
    }

    fun findAllToReview(challengeId: Long?): Flux<SolutionQL> {
        return solutionService.findAllToReview(challengeId!!)
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
            challengeService.findAllBySemesterIdAsQL(courseSemester.id!!)
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

    fun removeFeedback(reviewId: Long, feedbackId: Long) {
        TODO("Not yet implemented")
    }
}