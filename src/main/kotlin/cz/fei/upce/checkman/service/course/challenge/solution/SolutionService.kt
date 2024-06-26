package cz.fei.upce.checkman.service.course.challenge.solution

import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_PAGE
import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_PAGE_SIZE
import cz.fei.upce.checkman.domain.challenge.solution.Solution
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.review.Requirement
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.ReviewQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.SolutionQL
import cz.fei.upce.checkman.repository.challenge.solution.SolutionRepository
import cz.fei.upce.checkman.repository.review.FeedbackRepository
import cz.fei.upce.checkman.repository.review.RequirementRepository
import cz.fei.upce.checkman.repository.review.RequirementReviewRepository
import cz.fei.upce.checkman.repository.review.ReviewRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import cz.fei.upce.checkman.service.appuser.AppUserService
import cz.fei.upce.checkman.service.course.CourseService
import cz.fei.upce.checkman.service.course.security.CourseAuthorizationService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class SolutionService(
    private val solutionRepository: SolutionRepository,
    private val requirementReviewRepository: RequirementReviewRepository,
    private val requirementRepository: RequirementRepository,
    private val reviewRepository: ReviewRepository,
    private val feedbackRepository: FeedbackRepository,
    private val authorizationService: CourseAuthorizationService,
    private val appUserService: AppUserService,
    private val courseService: CourseService,
) {
    fun findById(id: Long): Mono<Solution> {
        return solutionRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
    }

    fun findById(id: Long, requester: AppUser): Mono<SolutionQL> {
        val hasReviewAccess = courseService.findBySolutionId(id)
            .flatMap {
                authorizationService.hasCourseAccess(
                    it.id!!,
                    requester,
                    listOf(CourseSemesterRole.Value.REVIEW_CHALLENGE.id)
                )
            }
            .switchIfEmpty(Mono.just(false))

        return hasReviewAccess.flatMap {
            if (it)
                solutionRepository.findById(id)
            else
                solutionRepository.findFirstByIdEqualsAndUserIdEquals(id, requester.id!!)
        }
            .flatMap { toSolutionQL(it) }
    }

    fun findByReviewId(reviewId: Long): Mono<Solution> {
        return solutionRepository.findByReview(reviewId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
    }

    fun updateStatus(reviewId: Long, status: Solution.Status): Mono<Solution> {
        return solutionRepository.findByReview(reviewId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap {
                it.statusId = status.id
                solutionRepository.save(it)
            }
    }

    fun findAllByChallengeAndUser(
        challengeId: Long,
        appUser: AppUser,
        pageSize: Int? = DEFAULT_PAGE_SIZE,
        page: Int? = DEFAULT_PAGE,
    ): Flux<SolutionQL> {


        return solutionRepository.findAllByChallengeIdEqualsAndUserIdEquals(challengeId, appUser.id!!)
            .flatMap { toSolutionQL(it) }
    }

    fun findAllByChallengeAndUser(challengeId: Long, appUser: AppUserQL): Flux<SolutionQL> {
        return findAllByChallengeAndUser(challengeId, appUser.toEntity())
    }

    fun findAllToReview(
        challengeId: Long,
        page: Int = DEFAULT_PAGE,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): Flux<Solution> {
        return solutionRepository.findAllToReview(challengeId, (page * pageSize), pageSize)
    }

    fun countToReview(challengeId: Long): Mono<Long> {
        return solutionRepository.countAllWithoutReview(challengeId)
    }

    private fun toSolutionQL(solution: Solution): Mono<SolutionQL> {
        val monoAuthor = appUserService.findById(solution.userId)

        val monoReview = findReviewAsQL(solution.id!!)
            .flatMap { review ->
                findAllRequirementsByChallengeId(solution.challengeId)
                    .switchIfEmpty(Flux.fromArray(emptyArray()))
                    .flatMap { requirement -> findReviewToRequirementsAsQL(review.id, requirement) }
                    .collectList()
                    .switchIfEmpty(Mono.just(listOf()))
                    .doOnNext { review.requirements = it }
                    .map { review }
            }

        return monoAuthor.flatMap { author ->
            monoReview.map { solution.toQL(author = author) }
                .flatMap { solutionQL ->
                    monoReview.map {
                        solutionQL.withReview(it)
                    }.switchIfEmpty(Mono.just(solutionQL))
                }.switchIfEmpty(Mono.just(solution.toQL(author = author)))
        }
    }

    fun findReviewAsQL(solutionId: Long): Mono<ReviewQL> {
        return reviewRepository.findFirstBySolutionIdEquals(solutionId)
            .flatMap { review ->
                feedbackRepository.findAllByReviewIdEquals(review.id!!)
                    .map { it.toQL() }
                    .collectList()
                    .switchIfEmpty(Mono.just(listOf()))
                    .map { review.toQL(feedbacks = it) }
            }
    }

    fun findAllByChallenge(
        challengeId: Long,
        pageSize: Int? = DEFAULT_PAGE_SIZE,
        page: Int? = DEFAULT_PAGE,
    ): Flux<Solution> {
        return solutionRepository.findAllByChallengeIdEquals(challengeId, page, pageSize)
    }

    private fun findAllRequirementsByChallengeId(challengeId: Long): Flux<Requirement> {
        return requirementRepository.findAllByChallengeIdEquals(challengeId)
    }

    private fun findReviewToRequirementsAsQL(reviewId: Long, requirement: Requirement): Mono<ReviewedRequirementQL> {
        return requirementReviewRepository.findFirstByReviewIdEqualsAndRequirementIdEquals(
            reviewId,
            requirement.id!!
        ).map { it.toReviewedRequirementQL(requirement.toQL()) }
    }

    fun findAllByAppUser(id: Long): Flux<Solution> {
        return solutionRepository.findAllByAppUserIdEquals(id)
    }
}
