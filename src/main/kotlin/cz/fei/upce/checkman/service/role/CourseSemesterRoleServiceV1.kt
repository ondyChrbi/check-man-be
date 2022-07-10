package cz.fei.upce.checkman.service.role

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.appuser.CourseSemesterRoleDtoV1
import cz.fei.upce.checkman.dto.appuser.CourseSemesterRolesResponseDtoV1
import cz.fei.upce.checkman.dto.course.CourseSemesterResponseDtoV1
import cz.fei.upce.checkman.graphql.output.course.CourseSemesterRolesQL
import cz.fei.upce.checkman.repository.course.AppUserCourseSemesterRoleRepository
import cz.fei.upce.checkman.repository.course.CourseSemesterRepository
import cz.fei.upce.checkman.repository.course.CourseSemesterRoleRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CourseSemesterRoleServiceV1(
    private val appUserCourseSemesterRoleRepository: AppUserCourseSemesterRoleRepository,
    private val courseSemesterRoleRepository: CourseSemesterRoleRepository,
    private val courseSemesterRepository: CourseSemesterRepository
) {

    fun findAllSemestersAndRolesAsDto(appUser: AppUser): Flux<CourseSemesterRolesResponseDtoV1> {
        return appUserCourseSemesterRoleRepository.findOnlySemestersByAppUserIEquals(appUser.id!!)
            .flatMap { findSemesterAndRolesAsDto(appUser, it.courseSemesterId) }
    }

    fun findAllSemestersAndRolesAsQL(appUser: AppUser): Flux<CourseSemesterRolesQL> {
        return appUserCourseSemesterRoleRepository.findOnlySemestersByAppUserIEquals(appUser.id!!)
            .flatMap { findSemesterAndRolesAsQL(appUser, it.courseSemesterId) }
    }

    fun findSemesterAndRolesAsDto(appUser: AppUser, semesterId: Long): Mono<CourseSemesterRolesResponseDtoV1> {
        return courseSemesterRepository.findById(semesterId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { CourseSemesterRolesResponseDtoV1(CourseSemesterResponseDtoV1.fromEntity(it)) }
            .flatMap { assignSemesterRolesAsDto(it, appUser) }
    }

    fun findSemesterAndRolesAsQL(appUser: AppUser, semesterId: Long): Mono<CourseSemesterRolesQL> {
        return courseSemesterRepository.findById(semesterId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { CourseSemesterRolesQL(it.toQL()) }
            .flatMap { assignSemesterRolesAsQL(it, appUser) }
    }

    fun findAllRolesAsDto(appUser: AppUser, semesterId: Long): Flux<CourseSemesterRoleDtoV1> {
        return appUserCourseSemesterRoleRepository
            .findAllByAppUserIdEqualsAndCourseSemesterIdEquals(appUser.id!!, semesterId)
            .flatMap { courseSemesterRoleRepository.findById(it.courseSemesterRoleId) }
            .map { CourseSemesterRoleDtoV1.fromEntity(it) }
    }
    private fun assignSemesterRolesAsDto(responseDto: CourseSemesterRolesResponseDtoV1, appUser: AppUser): Mono<CourseSemesterRolesResponseDtoV1> {
        return assignSemesterRolesAsDto(responseDto, appUser, responseDto.semester.id!!)
    }

    private fun assignSemesterRolesAsQL(ql: CourseSemesterRolesQL, appUser: AppUser): Mono<CourseSemesterRolesQL> {
        return assignSemesterRolesAsQL(ql, appUser, ql.semester.id)
    }

    private fun assignSemesterRolesAsDto(responseDto: CourseSemesterRolesResponseDtoV1, appUser: AppUser, semesterId: Long): Mono<CourseSemesterRolesResponseDtoV1> {
        return appUserCourseSemesterRoleRepository.findAllByAppUserIdEqualsAndCourseSemesterIdEquals(appUser.id!!, semesterId)
            .flatMap { courseSemesterRoleRepository.findById(it.courseSemesterRoleId) }
            .map { CourseSemesterRoleDtoV1.fromEntity(it) }
            .collectList()
            .map { responseDto.withRoles(it) }
    }

    private fun assignSemesterRolesAsQL(ql: CourseSemesterRolesQL, appUser: AppUser, semesterId: Long): Mono<CourseSemesterRolesQL> {
        return appUserCourseSemesterRoleRepository.findAllByAppUserIdEqualsAndCourseSemesterIdEquals(appUser.id!!, semesterId)
            .flatMap { courseSemesterRoleRepository.findById(it.courseSemesterRoleId) }
            .map { it.toQL() }
            .collectList()
            .doOnNext { ql.roles.addAll(it) }
            .map { ql }
    }
}