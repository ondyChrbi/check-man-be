package cz.fei.upce.checkman.service.course.challenge

import cz.fei.upce.checkman.component.rsql.ReactiveCriteriaRsqlSpecification
import cz.fei.upce.checkman.domain.challenge.Challenge
import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.course.challenge.ChallengeRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.ChallengeResponseDtoV1
import cz.fei.upce.checkman.repository.challenge.ChallengeRepository
import cz.fei.upce.checkman.repository.course.CourseSemesterRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ChallengeServiceV1(
    private val challengeRepository: ChallengeRepository,
    private val courseSemesterRepository: CourseSemesterRepository,
    private val authenticationService: AuthenticationServiceV1,
    private val entityTemplate: R2dbcEntityTemplate,
    private val reactiveCriteriaRsqlSpecification: ReactiveCriteriaRsqlSpecification
) {
    fun search(search: String?, courseId: Long, semesterId: Long): Flux<ChallengeResponseDtoV1> {
        val challenges = if (search == null || search.isEmpty())
            challengeRepository.findAll()
        else
            entityTemplate.select(Challenge::class.java)
                .matching(reactiveCriteriaRsqlSpecification.createCriteria(search))
                .all()

        return checkSemesterAccessibility(semesterId, courseId)
            .flatMapMany { challenges.map { ChallengeResponseDtoV1.fromEntity(it) } }
    }

    fun find(id: Long): Mono<ChallengeResponseDtoV1> {
        return challengeRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { ChallengeResponseDtoV1.fromEntity(it) }
    }

    fun add(challengeDto: ChallengeRequestDtoV1, courseId: Long, semesterId: Long, author: AppUser) =
        add(challengeDto.toResponseDto(), courseId, semesterId, author)

    fun add(challengeDto: ChallengeRequestDtoV1, courseId: Long, semesterId: Long, authentication: Authentication) =
        add(challengeDto, courseId, semesterId, authenticationService.extractAuthenticateUser(authentication))

    fun add(
        challengeDto: ChallengeResponseDtoV1, courseId: Long, semesterId: Long, author: AppUser
    ): Mono<ChallengeResponseDtoV1> {
        return checkSemesterAccessibility(semesterId, courseId)
            .flatMap { challengeRepository.save(challengeDto.toEntity(author, it.id!!)) }
            .map { challengeDto.withId(it.id) }
    }

    fun edit(challengeDto: ChallengeRequestDtoV1, courseId: Long, semesterId: Long, challengeId: Long) =
        edit(challengeDto.toResponseDto(), courseId, semesterId, challengeId)

    fun edit(
        challengeDto: ChallengeResponseDtoV1,
        courseId: Long,
        semesterId: Long,
        challengeId: Long
    ): Mono<ChallengeResponseDtoV1> {
        return checkSemesterAccessibility(semesterId, courseId)
            .flatMap { challengeRepository.findById(challengeId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { challengeRepository.save(challengeDto.toEntity(it)) }
            .map { challengeDto.withId(it.id) }
    }

    fun delete(courseId: Long, semesterId: Long, challengeId: Long): Mono<Void> {
        return checkSemesterExist(semesterId, courseId)
            .flatMap { challengeRepository.findById(challengeId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { challengeRepository.delete(it) }
    }

    private fun checkSemesterAccessibility(semesterId: Long, courseId: Long): Mono<CourseSemester> {
        return courseSemesterRepository.findFirstByIdEqualsAndCourseIdEquals(semesterId, courseId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
    }

    private fun checkSemesterExist(semesterId: Long, courseId: Long): Mono<Boolean> {
        return courseSemesterRepository.existsByIdEqualsAndCourseIdEquals(semesterId, courseId)
            .flatMap {
                if (it == false) {
                    Mono.error(ResourceNotFoundException())
                } else {
                    Mono.just(it)
                }
            }
    }
}
