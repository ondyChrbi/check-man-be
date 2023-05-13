package cz.fei.upce.checkman.repository.course

import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_SORT_FIELD
import cz.fei.upce.checkman.domain.course.Semester
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
interface SemesterRepository : ReactiveSortingRepository<Semester, Long> {
    fun findFirstByIdEqualsAndCourseIdEquals(courseSemesterId: Long, courseId: Long): Mono<Semester>
    fun findAllByCourseIdEquals(
        courseId: Long,
        sort: Sort = Sort.by(DEFAULT_SORT_FIELD),
        pageable: PageRequest? = null
    ): Flux<Semester>

    @Query(
        """
        select cs.id from course_semester cs
        inner join challenge ch on cs.id = ch.course_semester_id
        where ch.id = :challengeId
    """
    )
    fun findIdByChallengeId(challengeId: Long): Mono<Long>

    @Query(
        """
        select cs.id from course_semester cs
        inner join challenge ch on cs.id = ch.course_semester_id
        inner join requirement r on r.challenge_id = ch.id
        where r.id = :requirementId
    """
    )
    fun findIdByRequirementId(requirementId: Long): Mono<Long>

    @Query(
        """
        select cs.id from course_semester cs
        inner join challenge ch on cs.id = ch.course_semester_id
        inner join solution s on ch.id = s.challenge_id
        where s.id = :solutionId
    """
    )
    fun findIdBySolutionId(solutionId: Long): Mono<Long>

    @Query(
        """
        select cs.id from course_semester cs
        inner join challenge ch on cs.id = ch.course_semester_id
        inner join solution s on ch.id = s.challenge_id
        inner join review r on s.id = r.solution_id
        where r.id = :reviewId
    """
    )
    fun findIdByReviewId(reviewId: Long): Mono<Long>

    @Query(
        """
        select cs.id
        from course_semester cs
        inner join challenge ch on cs.id = ch.course_semester_id
        inner join solution s on ch.id = s.challenge_id
        inner join test_result tr on s.id = tr.solution_id
        where tr.id = :testResultId
    """
    )
    fun findIdByTestResultId(testResultId: Long): Mono<Long>

    @Query(
        """
        select cs.* from course_semester cs
        where :currentDate between cs.date_start and cs.date_end
        """
    )
    fun findAllAvailableToAppUser(currentDate: LocalDateTime, appUserId: Long): Flux<Semester>

    fun existsByIdEqualsAndCourseIdEquals(courseSemesterId: Long, courseId: Long): Mono<Boolean>

    @Query(
        """
        select cs.* from course_semester cs 
        inner join app_user_course_semester_role aucsr on cs.id = aucsr.course_semester_id
        where cs.course_id = :courseId and aucsr.app_user_id = :userId and aucsr.course_semester_role_id in (:rolesIds)
    """
    )
    fun findAllByUserHasRolesInCourse(courseId: Long, userId: Long, rolesIds: Collection<Long>): Flux<Semester>
}