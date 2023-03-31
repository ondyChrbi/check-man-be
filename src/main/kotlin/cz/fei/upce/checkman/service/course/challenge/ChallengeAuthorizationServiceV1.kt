package cz.fei.upce.checkman.service.course.challenge

import cz.fei.upce.checkman.domain.challenge.Challenge
import cz.fei.upce.checkman.domain.challenge.ChallengeKind
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.domain.user.GlobalRole
import cz.fei.upce.checkman.repository.challenge.ChallengeRepository
import cz.fei.upce.checkman.service.course.security.CourseAuthorizationServiceV1
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ChallengeAuthorizationServiceV1(
    private val challengeRepository: ChallengeRepository,
    private val courseAuthorizationService: CourseAuthorizationServiceV1
) {
    fun findAllByAppUserIsAuthorized(appUser: AppUser, semesterId: Long) : Flux<Challenge> {
        return courseAuthorizationService.findAllCourseSemesterRoles(appUser, semesterId)
            .mapNotNull { it.id!! }
            .collectList()
            .flatMapMany { courseRoles -> findAllByAppUserIsAuthorized(appUser, semesterId, courseRoles) }
    }

    fun findAllByAppUserIsAuthorized(appUser: AppUser, semesterId: Long, roles: MutableList<Long>) : Flux<Challenge> {
        if (canViewAllChallenges(roles)) {
            return challengeRepository.findAllByCourseSemesterIdEquals(semesterId)
        }

        var challengesByAuthor = Flux.empty<Challenge>()
        if (roles.contains(CourseSemesterRole.Value.CREATE_CHALLENGE.id)) {
            challengesByAuthor =  findAllByAppUserIsAuthor(appUser, semesterId)
        }

        val publishChallenges = findAllPublished(semesterId)

        return publishChallenges.mergeWith(challengesByAuthor).distinct()
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