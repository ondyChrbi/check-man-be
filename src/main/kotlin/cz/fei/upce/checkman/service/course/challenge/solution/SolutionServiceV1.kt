package cz.fei.upce.checkman.service.course.challenge.solution

import cz.fei.upce.checkman.domain.challenge.Solution
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.review.Requirement
import cz.fei.upce.checkman.domain.review.Review
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.graphql.output.challenge.requirement.ReviewedRequirementQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.SolutionQL
import cz.fei.upce.checkman.repository.challenge.SolutionRepository
import cz.fei.upce.checkman.repository.review.RequirementRepository
import cz.fei.upce.checkman.repository.review.RequirementReviewRepository
import cz.fei.upce.checkman.repository.review.ReviewRepository
import cz.fei.upce.checkman.service.course.CourseServiceV1
import cz.fei.upce.checkman.service.course.security.CourseAuthorizationServiceV1
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class SolutionServiceV1(
    private val solutionRepository: SolutionRepository,
    private val requirementReviewRepository: RequirementReviewRepository,
    private val requirementRepository: RequirementRepository,
    private val reviewRepository: ReviewRepository,
    private val authorizationService: CourseAuthorizationServiceV1,
    private val courseService: CourseServiceV1
) {
    fun findById(id: Long, requester: AppUser): Mono<SolutionQL> {
        val hasReviewAccess = courseService.findBySolutionId(id)
            .flatMap { authorizationService.hasCourseAccess(it.id!!, requester, listOf(CourseSemesterRole.Value.REVIEW_CHALLENGE.id)) }
            .switchIfEmpty(Mono.just(false))

        return hasReviewAccess.flatMap {
            if (it)
                solutionRepository.findById(id)
            else
                solutionRepository.findFirstByIdEqualsAndUserIdEquals(id, requester.id!!)
        }
        .flatMap { toReviewQL(it) }
    }

    fun findAllByChallengeAndUser(challengeId: Long, requester: AppUser): Flux<SolutionQL> {
        return solutionRepository.findAllByChallengeIdEqualsAndUserIdEquals(challengeId, requester.id!!)
            .map { it.toQL() }
    }

    fun findAllToReview(challengeId: Long): Flux<Solution> {
        return solutionRepository.findAllWithoutReview(challengeId)
    }

    fun countToReview(challengeId: Long): Mono<Long> {
        return solutionRepository.countAllWithoutReview(challengeId)
    }

    private fun toReviewQL(solution: Solution): Mono<SolutionQL> {
        return findReview(solution.id!!).flatMap { review ->
            findAllRequirementsByChallengeId(solution.challengeId)
                .flatMap { requirement -> findReviewToRequirementsAsQL(review, requirement) }
                .collectList()
                .map { review.toQL(it) }
                .map { solution.toQL(it) }
                .switchIfEmpty(Mono.just(solution.toQL()))
        }.switchIfEmpty(Mono.just(solution.toQL()))
    }

    private fun findReview(solutionId: Long): Mono<Review> {
        return reviewRepository.findFirstBySolutionIdEquals(solutionId)

    }

    private fun findAllRequirementsByChallengeId(challengeId: Long): Flux<Requirement> {
        return requirementRepository.findAllByChallengeIdEquals(challengeId)
    }

    private fun findReviewToRequirementsAsQL(review: Review, requirement: Requirement): Mono<ReviewedRequirementQL> {
        return requirementReviewRepository.findFirstByReviewIdEqualsAndRequirementIdEquals(
            review.id!!,
            requirement.id!!
        )
            .map { it.toReviewedRequirementQL(requirement.toQL()) }
    }
}
