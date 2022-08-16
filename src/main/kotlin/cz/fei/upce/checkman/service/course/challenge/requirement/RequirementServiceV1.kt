package cz.fei.upce.checkman.service.course.challenge.requirement

import cz.fei.upce.checkman.component.rsql.ReactiveCriteriaRSQLSpecification
import cz.fei.upce.checkman.domain.review.Requirement
import cz.fei.upce.checkman.dto.course.challenge.requirement.RequirementRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.requirement.RequirementResponseDtoV1
import cz.fei.upce.checkman.repository.review.RequirementRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import cz.fei.upce.checkman.service.course.challenge.ChallengeLocation
import cz.fei.upce.checkman.service.course.challenge.ChallengeServiceV1
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RequirementServiceV1(
    private val requirementRepository: RequirementRepository,
    private val challengeService: ChallengeServiceV1,
    private val entityTemplate: R2dbcEntityTemplate,
    private val reactiveCriteriaRsqlSpecification: ReactiveCriteriaRSQLSpecification
) {
    fun search(location: ChallengeLocation, search: String?): Flux<RequirementResponseDtoV1> {
        return challengeService.checkChallengeAssociation(location)
            .flatMapMany {
                if (search == null || search.isEmpty()) {
                    requirementRepository.findAllByChallengeIdEquals(location.challengeId)
                } else {
                    searchAllByChallengeId(location, search)
                }
            }
            .map { RequirementResponseDtoV1.fromEntity(it) }
    }

    private fun searchAllByChallengeId(location: ChallengeLocation, search: String): Flux<Requirement> {
        val condition = Criteria.where("challengeId").`is`(location.challengeId)

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
