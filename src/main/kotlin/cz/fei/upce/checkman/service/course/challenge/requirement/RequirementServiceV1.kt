package cz.fei.upce.checkman.service.course.challenge.requirement

import cz.fei.upce.checkman.dto.course.challenge.requirement.RequirementRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.requirement.RequirementResponseDtoV1
import cz.fei.upce.checkman.repository.review.RequirementRepository
import cz.fei.upce.checkman.service.course.challenge.ChallengeLocation
import cz.fei.upce.checkman.service.course.challenge.ChallengeServiceV1
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RequirementServiceV1(
    private val requirementRepository: RequirementRepository,
    private val challengeService: ChallengeServiceV1
) {
    fun add(location: ChallengeLocation, requirementDto: RequirementRequestDtoV1): Mono<RequirementResponseDtoV1> {
        return challengeService.checkChallengeAssociation(location)
            .flatMap { add(location, requirementDto.toResponseDto()) }
    }

    private fun add(location: ChallengeLocation, responseDto: RequirementResponseDtoV1): Mono<RequirementResponseDtoV1> {
        return requirementRepository.save(responseDto.toEntity(location.challengeId))
            .map { responseDto.withId(it.id) }
    }
}
