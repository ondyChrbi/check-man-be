package cz.fei.upce.checkman.repository.course

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.course.AppUserCourseSemesterRole
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface AppUserCourseSemesterRoleRepository : ReactiveSortingRepository<AppUserCourseSemesterRole, Long> {
    fun findDistinctByAppUserIdEqualsAndCourseSemesterRoleIdEquals(appUserId: Long, courseSemesterRoleId: Long, pageable: Pageable = CheckManApplication.DEFAULT_PAGEABLE): Flux<AppUserCourseSemesterRole>

    fun findAllByAppUserIdEqualsAndCourseSemesterIdEquals(
        appUserId: Long,
        courseSemesterId: Long
    ): Flux<AppUserCourseSemesterRole>

    fun existsByAppUserIdEqualsAndCourseSemesterIdEqualsAndCourseSemesterRoleIdEquals(
        appUserId: Long,
        courseSemesterId: Long,
        courseSemesterRoleId: Long
    ): Mono<Boolean>

    @Query("select distinct course_semester_id from app_user_course_semester_role where app_user_id = :appUserId")
    fun findOnlySemestersByAppUserIEquals(appUserId: Long): Flux<AppUserCourseSemesterRole>

    fun deleteByAppUserIdEqualsAndCourseSemesterIdEqualsAndCourseSemesterRoleIdEquals(
        appUserId: Long,
        courseSemesterId: Long,
        courseSemesterRoleId: Long
    ): Mono<Boolean>
}