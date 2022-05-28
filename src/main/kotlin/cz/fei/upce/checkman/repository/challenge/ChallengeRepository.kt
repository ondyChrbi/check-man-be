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

    fun existsByIdEqualsAndAndCourseSemesterIdEquals(id: Long, semesterId: Long): Mono<Boolean>

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
}