package cz.fei.upce.checkman.service.course.challenge.requirement

import cz.fei.upce.checkman.component.rsql.ReactiveCriteriaRSQLSpecification
import cz.fei.upce.checkman.domain.review.Requirement
import cz.fei.upce.checkman.domain.review.RequirementReview
import cz.fei.upce.checkman.dto.course.challenge.requirement.RequirementRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.requirement.RequirementResponseDtoV1
import cz.fei.upce.checkman.dto.graphql.input.course.RequirementInputQL
import cz.fei.upce.checkman.dto.graphql.input.course.challenge.solution.ReviewPointsInputQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL
import cz.fei.upce.checkman.repository.review.RequirementRepository
import cz.fei.upce.checkman.repository.review.RequirementReviewRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import cz.fei.upce.checkman.service.course.challenge.ChallengeLocation
import cz.fei.upce.checkman.service.course.challenge.exception.ChallengePublishedException
import cz.fei.upce.checkman.service.course.challenge.ChallengeService
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class RequirementService(
    private val requirementRepository: RequirementRepository,
    private val requirementReviewRepository: RequirementReviewRepository,
    private val challengeService: ChallengeService,
    private val entityTemplate: R2dbcEntityTemplate,
    private val reactiveCriteriaRsqlSpecification: ReactiveCriteriaRSQLSpecification
) {
    fun search(location: ChallengeLocation, search: String?): Flux<RequirementResponseDtoV1> {
        return challengeService.checkChallengeAssociation(location)
            .flatMapMany {
                if (search.isNullOrEmpty()) {
                    requirementRepository.findAllByChallengeIdEqualsAndActiveEquals(location.challengeId)
                } else {
                    searchAllByChallengeId(location, search)
                }
            }
            .map { RequirementResponseDtoV1.fromEntity(it) }
    }

    private fun searchAllByChallengeId(location: ChallengeLocation, search: String): Flux<Requirement> {
        val condition = Criteria.where("challengeId").`is`(location.challengeId)
            .and("removed").`is`("false")

        return entityTemplate.select(Requirement::class.java)
            .matching(reactiveCriteriaRsqlSpecification.createCriteria(search, condition))
            .all()
    }

    fun find(location: ChallengeLocation, requirementId: Long): Mono<RequirementResponseDtoV1> {
        return challengeService.checkChallengeAssociation(location)
            .flatMap { requirementRepository.findById(requirementId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { RequirementResponseDtoV1.fromEntity(it) }
    }

    fun add(location: ChallengeLocation, requirementDto: RequirementRequestDtoV1): Mono<RequirementResponseDtoV1> {
        return challengeService.checkChallengeAssociation(location)
            .flatMap { add(location, requirementDto.toResponseDto()) }
    }

    private fun add(
        location: ChallengeLocation,
        responseDto: RequirementResponseDtoV1
    ): Mono<RequirementResponseDtoV1> {
        return requirementRepository.save(responseDto.toEntity(location.challengeId))
            .map { responseDto.withId(it.id) }
    }

    fun update(
        location: ChallengeLocation,
        challengeId: Long,
        requirementDto: RequirementRequestDtoV1
    ): Mono<RequirementResponseDtoV1> = update(location, challengeId, requirementDto.toResponseDto())

    fun delete(location: ChallengeLocation, requirementId: Long): Mono<Void> {
        return challengeService.checkChallengeAssociation(location)
            .flatMap { requirementRepository.existsById(requirementId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap {
                if (!it) {
                    Mono.error(ResourceNotFoundException())
                } else {
                    requirementRepository.deleteById(requirementId)
                }
            }
    }

    fun addAsQL(challengeId: Long, input: cz.fei.upce.checkman.dto.graphql.input.course.RequirementInputQL): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL> {
        val isPublishedMono = challengeService.isPublished(challengeId)

        return isPublishedMono.flatMap {
            if (it)
                Mono.error(ChallengePublishedException(challengeId))
            else
                requirementRepository.save(input.toEntity(challengeId))
                    .map { it.toQL() }
        }
    }

    fun editAsQL(requirementId: Long, input: cz.fei.upce.checkman.dto.graphql.input.course.RequirementInputQL): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL> {

        return requirementRepository.findById(requirementId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { requirement ->
                challengeService.isPublished(requirement.challengeId)
                    .flatMap {
                        if (it)
                            Mono.error(ChallengePublishedException(requirement.challengeId))
                        else
                            Mono.just(requirement)
                    }
            }
            .map { input.toEntity(it.id!!, it.challengeId) }
            .flatMap { requirementRepository.save(it) }
            .map { it.toQL() }
    }

    fun findBySolutionIdAsQL(solutionId: Long): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL> {
        return requirementRepository.findAllBySolutionIdEquals(solutionId)
            .map { it.toQL() }
    }

    fun findByChallengeIdAsQL(challengeId: Long): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL> {
        return requirementRepository.findAllByChallengeIdEqualsAndRemovedEquals(challengeId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { it.toQL() }
    }

    fun findAllRequirementReviewsByReviewId(reviewId: Long): Flux<RequirementReview> {
        return requirementReviewRepository.findAllByReviewIdEquals(reviewId)
    }

    fun findAllRequirementReviewsByReviewIdAsQL(reviewId: Long): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.ReviewedRequirementQL> {
        return requirementReviewRepository.findAllByReviewIdEquals(reviewId)
            .map { it.toQL() }
    }

    fun findAllByChallengeIdAsQL(challengeId: Long): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL> {
        return requirementRepository.findAllByChallengeIdEqualsAndActiveEquals(challengeId)
            .map { it.toQL() }
    }

    fun findAllBySolutionIdAsQL(solutionId: Long): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL> {
        return requirementRepository.findAllBySolutionIdEquals(solutionId)
            .map { it.toQL() }
    }

    fun removeAsQL(requirementId: Long): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL> {
        val isPublished = challengeService.isPublishedByReview(requirementId)

        return isPublished.flatMap {
            if (it)
                Mono.error(ChallengePublishedException("Challenge already published"))
            else
                requirementRepository.disableRequirement(requirementId)
                    .map { it.toQL() }
                    .toMono()
        }
    }

    fun editReviewPoints(reviewId: Long, requirementId: Long, reviewPoints: cz.fei.upce.checkman.dto.graphql.input.course.challenge.solution.ReviewPointsInputQL): Mono<Boolean> {
        val validationCheck = checkRequirementPoints(requirementId, reviewPoints)
        val alreadyCreated = requirementReviewRepository.findByReviewIdEqualsAndRequirementIdEquals(reviewId, requirementId)


        return validationCheck.collectList()
            .flatMap {
                alreadyCreated
                    .switchIfEmpty(Mono.just(reviewPoints.toEntity(reviewId, requirementId)))
                    .doOnNext { it.point = reviewPoints.points }
                    .flatMap { requirementReviewRepository.save(it) }
                    .map { true }
            }

    }


    fun findByReviewedRequirementIdAsQL(id: Long): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.requirement.RequirementQL> {
        return requirementRepository.findByReviewedRequirementId(id)
            .map { it.toQL() }
    }

    private fun checkRequirementPoints(requirementId: Long, reviewPoints: cz.fei.upce.checkman.dto.graphql.input.course.challenge.solution.ReviewPointsInputQL): Flux<Boolean> {
        val points = reviewPoints.points

        val minCheck = if (points > 0)
            Mono.just(true)
        else
            Mono.error(NonPositiveRequirementPointsException(points))

        val maxCheck = requirementRepository.findById(requirementId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap {
                if (points <= it.maxPoint)
                    Mono.just(true)
                else
                    Mono.error(MaximumRequirementPointsReachedException(points, it.maxPoint))
            }

        return Flux.concat(minCheck, maxCheck)
    }

    private fun update(
        location: ChallengeLocation,
        challengeId: Long,
        requirementDto: RequirementResponseDtoV1
    ): Mono<RequirementResponseDtoV1> {
        return challengeService.checkChallengeAssociation(location)
            .flatMap { requirementRepository.findById(challengeId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { requirementRepository.save(requirementDto.toEntity(it)) }
            .map { requirementDto.withId(it.id) }
    }
}
