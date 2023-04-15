package cz.fei.upce.checkman.service.course.challenge

import cz.fei.upce.checkman.component.rsql.ReactiveCriteriaRSQLSpecification
import cz.fei.upce.checkman.domain.challenge.Challenge
import cz.fei.upce.checkman.domain.challenge.ChallengeKind
import cz.fei.upce.checkman.domain.challenge.PermittedAppUserChallenge
import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.domain.user.GlobalRole
import cz.fei.upce.checkman.dto.course.challenge.ChallengeRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.ChallengeResponseDtoV1
import cz.fei.upce.checkman.dto.course.challenge.PermitAppUserChallengeRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.RemoveAccessAppUserChallengeRequestDtoV1
import cz.fei.upce.checkman.graphql.input.course.challenge.ChallengeInputQL
import cz.fei.upce.checkman.graphql.output.challenge.ChallengeQL
import cz.fei.upce.checkman.repository.challenge.ChallengeRepository
import cz.fei.upce.checkman.repository.challenge.PermittedAppUserChallengeRepository
import cz.fei.upce.checkman.repository.course.CourseSemesterRepository
import cz.fei.upce.checkman.repository.user.AppUserRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime

@Service
class ChallengeServiceV1(
    private val challengeRepository: ChallengeRepository,
    private val appUserRepository: AppUserRepository,
    private val courseSemesterRepository: CourseSemesterRepository,
    private val permittedAppUserChallengeRepository: PermittedAppUserChallengeRepository,
    private val entityTemplate: R2dbcEntityTemplate,
    private val reactiveCriteriaRsqlSpecification: ReactiveCriteriaRSQLSpecification,
    private val challengeAuthorizationService: ChallengeAuthorizationServiceV1
) {
    fun search(search: String?, courseId: Long, semesterId: Long): Flux<ChallengeResponseDtoV1> {
        val challenges = if (search.isNullOrEmpty())
            challengeRepository.findAll()
        else
            entityTemplate.select(Challenge::class.java)
                .matching(reactiveCriteriaRsqlSpecification.createCriteria(search))
                .all()

        return findCourseSemester(semesterId, courseId)
            .flatMapMany { challenges.map { ChallengeResponseDtoV1.fromEntity(it) } }
    }

    fun search(
        search: String?,
        courseId: Long,
        semesterId: Long,
        appUser: AppUser,
        authorities: Set<GlobalRole>
    ): Flux<ChallengeResponseDtoV1> {
        if (VIEW_PERMISSIONS.intersect(authorities.map { it.name }.toSet()).isNotEmpty()) {
            return search(search, courseId, semesterId)
        }

        return findCourseSemester(semesterId, courseId)
            .flatMapMany { findAllRelatedTo(semesterId, appUser) }
            .map { ChallengeResponseDtoV1.fromEntity(it) }
    }

    fun findById(id: Long): Mono<Challenge> {
        return challengeRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
    }

    fun add(courseId: Long, semesterId: Long, author: AppUser, challengeDto: ChallengeRequestDtoV1) =
        add(courseId, semesterId, author, challengeDto.toResponseDto())

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
                if (!it) {
                    Mono.error(ResourceNotFoundException())
                } else {
                    Mono.just(it)
                }
            }
    }

    private fun findAllRelatedTo(courseSemesterId: Long, appUser: AppUser): Flux<Challenge> {
        val private = ChallengeKind.Value.PRIVATE.map { it.id }
        val public = ChallengeKind.Value.PUBLIC.map { it.id }

        return challengeRepository.findAllByCourseSemesterIdEqualsAndChallengeKindIdIsIn(courseSemesterId, private)
            .concatWith(
                challengeRepository.findAllByAppUserPermittedByCourseSemester(
                    courseSemesterId,
                    appUser.id!!,
                    public
                )
            )
    }

    fun checkChallengeAssociation(location: ChallengeLocation) =
        checkChallengeAssociation(location.courseId, location.semesterId, location.challengeId)

    fun checkChallengeAssociation(courseId: Long, semesterId: Long, challengeId: Long): Mono<Boolean> {
        return checkSemesterExist(courseId, semesterId).flatMap {
            challengeRepository.existsByIdEqualsAndAndCourseSemesterIdEquals(challengeId, semesterId)
                .flatMap {
                    if (!it) {
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
        permitted: PermittedAppUserChallenge,
        accessTo: LocalDateTime
    ): Mono<PermittedAppUserChallenge> {
        permitted.accessTo = accessTo

        return permittedAppUserChallengeRepository.save(permitted)
    }

    fun permitToAccess(appUserId: Long, challengeId: Long, accessTo: LocalDateTime): Mono<PermittedAppUserChallenge> {
        return permittedAppUserChallengeRepository.save(
            PermittedAppUserChallenge(
                appUserId = appUserId, challengeId = challengeId, accessTo = accessTo
            )
        )
    }

    fun removeAccessFrom(appUserId: Long, challengeId: Long): Mono<Void> {
        return Mono.empty()
    }

    fun findAllBySemesterIdAsQL(semesterId: Long, requester: AppUser): Flux<ChallengeQL> {
        return challengeAuthorizationService.findAllByAppUserIsAuthorized(requester, semesterId)
            .map { it.toQL() }
    }

    fun findByIdAsQL(id: Long): Mono<ChallengeQL> {
        return challengeRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { it.toQL() }
    }

    private fun assignAuthor(challenge: Challenge): Mono<ChallengeQL> {
        return appUserRepository.findById(challenge.authorId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { challenge.toQL(it.toQL()) }
    }

    fun addAsQL(semesterId: Long, input: ChallengeInputQL, author: AppUser): Mono<ChallengeQL> {
        return challengeRepository.save(input.toEntity(semesterId, author))
            .map { it.toQL(author.toQL(), emptyList()) }
    }

    fun editAsQL(challengeId: Long, input: ChallengeInputQL, appUser: AppUser): Mono<ChallengeQL> {
        return challengeRepository.findById(challengeId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { input.toEntity(it.courseSemesterId!!, challengeId, appUser) }
            .flatMap { challengeRepository.save(it) }
            .map { it.toQL(appUser.toQL(), emptyList()) }
    }

    fun deleteAsQL(challengeId: Long, appUser: AppUser): Mono<ChallengeQL> {
        return challengeRepository.disableChallenge(challengeId)
            .flatMap { assignAuthor(it) }
            .toMono()
    }

    fun publish(challengeId: Long, extractAuthenticateUser: AppUser): Mono<Boolean> {
        return challengeRepository.findById(challengeId)
            .flatMap { challenge -> checkChallenge(challenge, extractAuthenticateUser) }
            .flatMap { challenge ->
                challenge.published = true
                challengeRepository.save(challenge)
            }.map { true }
    }

    fun isPublished(challengeId: Long) : Mono<Boolean>{
        return challengeRepository.findById(challengeId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { it.published }
    }

    fun isPublishedByReview(reviewId: Long) : Mono<Boolean>{
        return challengeRepository.findByReviewId(reviewId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { it.published }
    }

    fun existById(challengeId: Long): Mono<Boolean> {
        return challengeRepository.existsById(challengeId)
    }

    fun checkExist(challengeId: Long): Mono<Boolean> {
        return challengeRepository.existsById(challengeId)
            .flatMap {
                if (!it) {
                    Mono.error(ResourceNotFoundException())
                } else {
                    Mono.just(it)
                }
            }
    }

    fun findByPermittedAppUserChallengeIdAsQL(id: Long): Mono<ChallengeQL> {
        return challengeRepository.findByPermittedAppUserChallenge(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { it.toQL() }
    }

    private fun checkChallenge(challenge: Challenge, extractAuthenticateUser: AppUser): Mono<Challenge> {
        return if (challenge.authorId != extractAuthenticateUser.id)
            Mono.error(UserNotAuthorException(challenge.id!!))
        else if (challenge.published)
            Mono.error(AlreadyPublishedException(challenge.id!!))
        else
            Mono.just(challenge)
    }

    companion object {
        val VIEW_PERMISSIONS = setOf(
            GlobalRole.ROLE_COURSE_MANAGE, GlobalRole.ROLE_COURSE_SEMESTER_MANAGE,
            GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE, GlobalRole.ROLE_COURSE_CHALLENGE_VIEW
        )
    }
}
