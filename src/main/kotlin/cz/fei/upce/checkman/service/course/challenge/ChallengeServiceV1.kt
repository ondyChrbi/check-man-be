package cz.fei.upce.checkman.service.course.challenge

import cz.fei.upce.checkman.component.rsql.ReactiveCriteriaRsqlSpecification
import cz.fei.upce.checkman.domain.challenge.Challenge
import cz.fei.upce.checkman.domain.challenge.PermittedAppUserChallenge
import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.course.challenge.ChallengeRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.ChallengeResponseDtoV1
import cz.fei.upce.checkman.dto.course.challenge.PermitAppUserChallengeRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.RemoveAccessAppUserChallengeRequestDtoV1
import cz.fei.upce.checkman.repository.challenge.ChallengeRepository
import cz.fei.upce.checkman.repository.challenge.PermittedAppUserChallengeRepository
import cz.fei.upce.checkman.repository.course.CourseSemesterRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class ChallengeServiceV1(
    private val challengeRepository: ChallengeRepository,
    private val courseSemesterRepository: CourseSemesterRepository,
    private val permittedAppUserChallengeRepository: PermittedAppUserChallengeRepository,
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

        return findCourseSemester(semesterId, courseId)
            .flatMapMany { challenges.map { ChallengeResponseDtoV1.fromEntity(it) } }
    }

    fun find(id: Long): Mono<Challenge> {
        return challengeRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
    }

    fun add(courseId: Long, semesterId: Long, author: AppUser, challengeDto: ChallengeRequestDtoV1) =
        add(courseId, semesterId, author, challengeDto.toResponseDto())

    fun add(courseId: Long, semesterId: Long, authentication: Authentication, challengeDto: ChallengeRequestDtoV1) =
        add(courseId, semesterId, authenticationService.extractAuthenticateUser(authentication), challengeDto)

    fun add(
        courseId: Long, semesterId: Long, author: AppUser, challengeDto: ChallengeResponseDtoV1
    ): Mono<ChallengeResponseDtoV1> {
        return findCourseSemester(semesterId, courseId)
            .flatMap { challengeRepository.save(challengeDto.toEntity(author, it.id!!)) }
            .map { challengeDto.withId(it.id) }
    }

    fun edit(courseId: Long, semesterId: Long, challengeId: Long, challengeDto: ChallengeRequestDtoV1) =
        edit(courseId, semesterId, challengeId, challengeDto.toResponseDto())

    fun edit(
        courseId: Long,
        semesterId: Long,
        challengeId: Long,
        challengeDto: ChallengeResponseDtoV1
    ): Mono<ChallengeResponseDtoV1> {
        return findCourseSemester(semesterId, courseId)
            .flatMap { challengeRepository.findById(challengeId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { challengeRepository.save(challengeDto.toEntity(it)) }
            .map { challengeDto.withId(it.id) }
    }

    fun delete(courseId: Long, semesterId: Long, challengeId: Long): Mono<Void> {
        return checkSemesterExist(courseId, semesterId)
            .flatMap { challengeRepository.findById(challengeId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { challengeRepository.delete(it) }
    }

    fun findCourseSemester(semesterId: Long, courseId: Long): Mono<CourseSemester> {
        return courseSemesterRepository.findFirstByIdEqualsAndCourseIdEquals(semesterId, courseId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
    }

    fun permitAccess(
        location: ChallengeLocation,
        permitDto: PermitAppUserChallengeRequestDtoV1
    ): Mono<PermittedAppUserChallenge> {
        return checkChallengeAssociation(location).flatMap {
            permitOrExtendAccess(
                permitDto.appUserId,
                location.challengeId,
                permitDto.accessTo
            )
        }
    }

    fun removeAccess(location: ChallengeLocation, removeDto: RemoveAccessAppUserChallengeRequestDtoV1): Mono<Void> {
        return checkChallengeAssociation(location).flatMap {
            removeAccessFrom(
                removeDto.appUserId,
                location.challengeId
            )
        }
    }

    fun checkSemesterExist(courseId: Long, semesterId: Long): Mono<Boolean> {
        return courseSemesterRepository.existsByIdEqualsAndCourseIdEquals(semesterId, courseId)
            .flatMap {
                if (it == false) {
                    Mono.error(ResourceNotFoundException())
                } else {
                    Mono.just(it)
                }
            }
    }

    fun checkChallengeAssociation(location: ChallengeLocation) =
        checkChallengeAssociation(location.courseId, location.semesterId, location.challengeId)

    fun checkChallengeAssociation(courseId: Long, semesterId: Long, challengeId: Long): Mono<Boolean> {
        return checkSemesterExist(courseId, semesterId).flatMap {
            challengeRepository.existsByIdEqualsAndAndCourseSemesterIdEquals(challengeId, semesterId)
                .flatMap {
                    if (it == false) {
                        Mono.error(ResourceNotFoundException())
                    } else {
                        Mono.just(it)
                    }
                }
        }
    }

    private fun permitOrExtendAccess(appUserId: Long, challengeId: Long, accessTo: LocalDateTime) =
        permittedAppUserChallengeRepository
            .findFirstByAppUserIdEqualsAndChallengeIdEquals(appUserId, challengeId)
            .switchIfEmpty(permitToAccess(appUserId, challengeId, accessTo))
            .flatMap { extendAccess(it, accessTo) }

    private fun extendAccess(
        permittedAppUserChallenge: PermittedAppUserChallenge,
        accessTo: LocalDateTime
    ): Mono<PermittedAppUserChallenge> {
        permittedAppUserChallenge.accessTo = accessTo

        return permittedAppUserChallengeRepository.save(permittedAppUserChallenge)
    }

    private fun permitToAccess(appUserId: Long, challengeId: Long, accessTo: LocalDateTime) =
        permittedAppUserChallengeRepository.save(
            PermittedAppUserChallenge(
                appUserId = appUserId, challengeId = challengeId, accessTo = accessTo
            )
        )

    private fun removeAccessFrom(appUserId: Long, challengeId: Long) =
        permittedAppUserChallengeRepository.existsByAppUserIdEqualsAndChallengeIdEquals(appUserId, challengeId)
            .flatMap {
                if (it == false) {
                    Mono.error(AppUserDoesntHaveAccessToChallengeException())
                } else {
                    permittedAppUserChallengeRepository.deleteAllByAppUserIdEqualsAndChallengeIdEquals(
                        appUserId,
                        challengeId
                    )
                }
            }
}
