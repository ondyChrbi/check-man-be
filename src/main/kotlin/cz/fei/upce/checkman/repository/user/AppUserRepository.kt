package cz.fei.upce.checkman.repository.user

import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_OFFSET
import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_SIZE
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.user.AppUser
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface AppUserRepository : ReactiveCrudRepository<AppUser, Long> {
    fun findByStagIdEquals(stagId : String) : Mono<AppUser>

    @Query("""
        select distinct au.* from app_user au
        inner join app_user_course_semester_role aucsr on au.id = aucsr.app_user_id
        inner join course_semester cs on aucsr.course_semester_id = cs.id
        where cs.id = :semesterId
        limit :size offset :offset
    """)
    fun findAllByCourseSemester(semesterId: Long, offset: Int = DEFAULT_OFFSET, size: Int = DEFAULT_SIZE): Flux<AppUser>

    @Query("""
        select au.* from app_user au
        inner join challenge c on au.id = c.author_id
        where c.id = :challengeId limit 1
    """)
    fun findAuthorByChallengeId(challengeId: Long) : Mono<AppUser>

    @Query("""
        select distinct au.* from app_user au
        inner join app_user_course_semester_role aucsr on au.id = aucsr.app_user_id
        inner join challenge c on c.course_semester_id = aucsr.course_semester_id
        where aucsr.course_semester_role_id = :role and aucsr.app_user_id not in (
            select distinct au.id from app_user au
            inner join permitted_app_user_challenge pauc on au.id = pauc.app_user_id
            where pauc.challenge_id = :challengeId
        );
    """)
    fun findAllPermitToChallenge(challengeId: Long, roleId: Long = CourseSemesterRole.Value.ACCESS.id) : Flux<AppUser>

    @Query("""
        select distinct au.* from app_user au
        inner join permitted_app_user_challenge pauc on au.id = pauc.app_user_id
        where pauc.challenge_id = :challengeId
    """)
    fun findAllPermittedToChallenge(challengeId: Long): Flux<AppUser>

    @Query("""
        select au.* from app_user au
        inner join permitted_app_user_challenge pauc on au.id = pauc.app_user_id
        where pauc.id = :id
        limit 1
    """)
    fun findByPermittedChallengeId(id: Long) : Flux<AppUser>
}