package cz.fei.upce.checkman.service.course.challenge

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.challenge.Challenge
import cz.fei.upce.checkman.domain.challenge.ChallengeKind
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.domain.user.GlobalRole
import cz.fei.upce.checkman.repository.challenge.ChallengeRepository
import cz.fei.upce.checkman.service.course.security.CourseAuthorizationService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ChallengeAuthorizationService(
    private val challengeRepository: ChallengeRepository,
    private val courseAuthorizationService: CourseAuthorizationService,
    private val permitChallengeService: PermitChallengeService,
) {
    fun findAllByAppUserIsAuthorized(appUser: AppUser, semesterId: Long) : Flux<Challenge> {
        return courseAuthorizationService.findAllCourseSemesterRoles(appUser, semesterId)
            .mapNotNull { it.id!! }
            .collectList()
            .flatMapMany { courseRoles -> findAllByAppUserIsAuthorized(appUser, semesterId, courseRoles) }
    }

    fun findAllByAppUserIsAuthorized(
        appUser: AppUser,
        semesterId: Long,
        roles: MutableList<Long>,
    ): Flux<Challenge> {

        if (canViewAllChallenges(roles)) {
            return challengeRepository.findAllByCourseSemesterIdEquals(semesterId)
        }

        var challengesByAuthor = Flux.empty<Challenge>()
        if (roles.contains(CourseSemesterRole.Value.CREATE_CHALLENGE.id)) {
            challengesByAuthor =  findAllByAppUserIsAuthor(appUser, semesterId)
        }

        var protectedChallenges = Flux.empty<Challenge>()
        if (canSeeProtectedChallenges(roles)) {
            protectedChallenges = challengeRepository.findAllProtectedByCourseSemesterIdEquals(semesterId, retrieveProtectedKinds(roles))
        }

        val publishChallenges = findAllPublished(semesterId)

        return publishChallenges.mergeWith(protectedChallenges)
            .mergeWith(challengesByAuthor)
            .distinct()
    }

    fun isAuthorizedToView(challenge: Challenge, appUser: AppUser, roles: MutableList<Long>): Mono<Boolean> {
        if (isAuthor(challenge, appUser)) {
            return Mono.just(true)
        }

        if (!challenge.published && canSeeNotPublished(challenge, roles)) {
            return Mono.just(true)
        }

        if (challenge.published) {
            if (isPublicChallenge(challenge)) {
                return Mono.just(true)
            }

            if (canSeeProtectedChallenges(challenge, roles)) {
                return Mono.just(true)
            }

            return isPermittedToView(challenge, appUser)
        }

        return Mono.just(false)
    }

    fun isAuthorizedToView(challenge: Challenge, appUser: AppUser): Mono<Boolean> {
        val roles = courseAuthorizationService.findAllCourseSemesterRoles(appUser, challenge.courseSemesterId!!)

        return roles.map { it.id!! }
            .collectList()
            .flatMap {  isAuthorizedToView(challenge, appUser, it) }
    }

    private fun retrieveProtectedKinds(roles: MutableList<Long>) : Collection<Long> {
        val kinds = mutableListOf<ChallengeKind.Value>()

        if (roles.contains(CourseSemesterRole.Value.PERMIT_CHALLENGE_CREDIT.id)) {
            kinds.add(ChallengeKind.Value.CREDIT)
        }

        if (roles.contains(CourseSemesterRole.Value.PERMIT_CHALLENGE_EXAM.id)) {
            kinds.add(ChallengeKind.Value.EXAM)
        }

        return kinds.map { it.id }
    }

    private fun canSeeProtectedChallenges(challenge: Challenge, roles: Iterable<Long>): Boolean {
        return (challenge.challengeKindId == ChallengeKind.Value.EXAM.id && roles.contains(CourseSemesterRole.Value.PERMIT_CHALLENGE_EXAM.id)) ||
            (challenge.challengeKindId == ChallengeKind.Value.CREDIT.id && roles.contains(CourseSemesterRole.Value.PERMIT_CHALLENGE_CREDIT.id))
    }

    private fun canSeeProtectedChallenges(roles: Iterable<Long>): Boolean {
        return (roles.contains(CourseSemesterRole.Value.PERMIT_CHALLENGE_EXAM.id)) ||
            (roles.contains(CourseSemesterRole.Value.PERMIT_CHALLENGE_CREDIT.id))
    }

    private fun canSeeNotPublished(challenge: Challenge, roles: MutableList<Long>): Boolean {
        return roles.contains(CourseSemesterRole.Value.CREATE_CHALLENGE.id) && !challenge.published
    }

    private fun isAuthor(challenge: Challenge, appUser: AppUser): Boolean {
        return challenge.authorId == appUser.id
    }

    private fun isPublicChallenge(challenge: Challenge): Boolean {
        return challenge.challengeKindId == ChallengeKind.Value.MANDATORY.id || challenge.challengeKindId == ChallengeKind.Value.OPTIONAL.id
    }

    private fun isPermittedToView(challenge: Challenge, appUser: AppUser): Mono<Boolean> {
        if (isPublicChallenge(challenge)) {
            return Mono.just(true)
        }

        return permitChallengeService.isPermitted(challenge, appUser);
    }

    private fun findAllByAppUserIsAuthor(appUser: AppUser, semesterId: Long): Flux<Challenge> {
        return challengeRepository.findAllByAuthorIdEqualsAndCourseSemesterIdEquals(appUser.id!!, semesterId)
    }

    private fun findAllPublished(semesterId: Long): Flux<Challenge> {
        val permittedKindIs = PUBLISH_KINDS.map { it.id }

        return challengeRepository.findAllByCourseSemesterIdEqualsAndChallengeKindIdIsInAndPublishedEquals(
            semesterId, permittedKindIs
        )
    }

    private fun canViewAllChallenges(roles: Iterable<Long>): Boolean {
        return roles.any { role -> role in VIEW_ALL_ROLES.map { it.id } }
    }

    private companion object {
        val VIEW_ALL_ROLES = mutableListOf(CourseSemesterRole.Value.EDIT_COURSE, CourseSemesterRole.Value.EDIT_CHALLENGE)
        val PUBLISH_KINDS = mutableListOf(ChallengeKind.Value.OPTIONAL, ChallengeKind.Value.MANDATORY)
        val VIEW_ALL_GLOBAL_ROLES = mutableListOf(GlobalRole.ROLE_COURSE_VIEW, GlobalRole.ROLE_COURSE_MANAGE,
            GlobalRole.ROLE_COURSE_CHALLENGE_VIEW, GlobalRole.ROLE_COURSE_SEMESTER_VIEW,
            GlobalRole.ROLE_COURSE_SEMESTER_MANAGE, GlobalRole.ROLE_COURSE_ACCESS, GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE)
    }
}