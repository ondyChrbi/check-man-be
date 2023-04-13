package cz.fei.upce.checkman.repository.challenge

import cz.fei.upce.checkman.domain.challenge.Challenge
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ChallengeRepository : ReactiveCrudRepository<Challenge, Long> {
    fun findAllByCourseSemesterIdEqualsAndChallengeKindIdIsIn(
        courseSemesterId: Long,
        challengeKindsIds: Collection<Long>
    ): Flux<Challenge>

    fun findAllByCourseSemesterIdEqualsAndChallengeKindIdIsInAndPublishedEquals(
        courseSemesterId: Long,
        challengeKindsIds: Collection<Long>,
        published: Boolean = true
    ): Flux<Challenge>

    fun findAllByCourseSemesterIdEquals(semesterId: Long): Flux<Challenge>

    fun existsByIdEqualsAndAndCourseSemesterIdEquals(id: Long, semesterId: Long): Mono<Boolean>

    fun findAllByCourseSemesterIdEqualsAndActiveEquals(semesterId: Long, active: Boolean = true): Flux<Challenge>

    fun findAllByAuthorIdEqualsAndCourseSemesterIdEquals(appUserId: Long, semesterId: Long): Flux<Challenge>

    fun findAllByCourseSemesterIdEqualsAndActiveAndPublishedEquals(semesterId: Long, active: Boolean = true, published : Boolean = true): Flux<Challenge>

    @Query("""
        update challenge set active = false where id = :id returning *
    """)
    fun disableChallenge(id: Long) : Flux<Challenge>

    @Query(
        """
        select c.* from challenge c
        inner join permitted_app_user_challenge pauc on c.id = pauc.challenge_id
        where c.course_semester_id = :courseSemesterId
        and pauc.app_user_id = :appUserId
        and c.challenge_kind_id in (:challengeKindsIds)
    """
    )
    fun findAllByAppUserPermittedByCourseSemester(
        courseSemesterId: Long,
        appUserId: Long,
        challengeKindsIds: Collection<Long>
    ): Flux<Challenge>

    @Query("""
        select ch.* from challenge ch
        inner join requirement r on ch.id = r.challenge_id
        where r.id = : requirementId limit 1
    """)
    fun findByReviewId(requirementId: Long): Mono<Challenge>

    @Query("""
        select ch.* from challenge ch
        inner join permitted_app_user_challenge pauc on ch.id = pauc.challenge_id
        where pauc.id = :id
    """)
    fun findByPermittedAppUserChallenge(id: Long): Mono<Challenge>
}