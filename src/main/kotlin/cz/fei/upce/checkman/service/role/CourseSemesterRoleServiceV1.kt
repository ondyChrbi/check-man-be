package cz.fei.upce.checkman.service.role

import cz.fei.upce.checkman.domain.course.AppUserCourseSemesterRole
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.appuser.CourseSemesterRoleDtoV1
import cz.fei.upce.checkman.dto.appuser.CourseSemesterRolesResponseDtoV1
import cz.fei.upce.checkman.dto.course.CourseSemesterResponseDtoV1
import cz.fei.upce.checkman.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.graphql.output.course.CourseSemesterRoleQL
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
    fun hasRole(appUser: AppUser, semesterId: Long, role: CourseSemesterRole.Value): Mono<Boolean> {
        return appUserCourseSemesterRoleRepository.existsByAppUserIdEqualsAndCourseSemesterIdEqualsAndCourseSemesterRoleIdEquals(
            appUser.id!!,
            semesterId,
            role.id
        )
    }


    fun findAllRolesByUserAndCourseSemesterAsQL(appUser: AppUserQL, semesterId: Long): Flux<CourseSemesterRoleQL> {
        return courseSemesterRoleRepository.findAllRelatedToUserAndCourseSemester(appUser.id!!, semesterId)
            .map { it.toQL() }
    }

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

    fun addRole(appUserId: Long, semesterId: Long, roleId: Long): Mono<Boolean> {
        return appUserCourseSemesterRoleRepository.existsByAppUserIdEqualsAndCourseSemesterIdEqualsAndCourseSemesterRoleIdEquals(
            appUserId,
            semesterId,
            roleId
        ).flatMap {
            if (it)
                Mono.error(RoleAlreadyAssignedException(appUserId, roleId))
            else
                appUserCourseSemesterRoleRepository.save(
                    AppUserCourseSemesterRole(
                        appUserId = appUserId, courseSemesterId = semesterId, courseSemesterRoleId = roleId
                    )
                )
        }.map { true }
    }

    fun removeRole(appUserId: Long, roleId: Long, semesterId: Long): Mono<Boolean> {
        return appUserCourseSemesterRoleRepository.existsByAppUserIdEqualsAndCourseSemesterIdEqualsAndCourseSemesterRoleIdEquals(
            appUserId,
            semesterId,
            roleId
        ).flatMap {
            if (!it)
                Mono.error(RoleNotAssignedException(appUserId, roleId))
            else
                appUserCourseSemesterRoleRepository.deleteByAppUserIdEqualsAndCourseSemesterIdEqualsAndCourseSemesterRoleIdEquals(
                    appUserId = appUserId, courseSemesterId = semesterId, courseSemesterRoleId = roleId
                )
        }
    }

    private fun assignSemesterRolesAsDto(
        responseDto: CourseSemesterRolesResponseDtoV1,
        appUser: AppUser
    ): Mono<CourseSemesterRolesResponseDtoV1> {
        return assignSemesterRolesAsDto(responseDto, appUser, responseDto.semester.id!!)
    }

    private fun assignSemesterRolesAsQL(ql: CourseSemesterRolesQL, appUser: AppUser): Mono<CourseSemesterRolesQL> {
        return assignSemesterRolesAsQL(ql, appUser, ql.semester.id)
    }

    private fun assignSemesterRolesAsDto(
        responseDto: CourseSemesterRolesResponseDtoV1,
        appUser: AppUser,
        semesterId: Long
    ): Mono<CourseSemesterRolesResponseDtoV1> {
        return appUserCourseSemesterRoleRepository.findAllByAppUserIdEqualsAndCourseSemesterIdEquals(
            appUser.id!!,
            semesterId
        )
            .flatMap { courseSemesterRoleRepository.findById(it.courseSemesterRoleId) }
            .map { CourseSemesterRoleDtoV1.fromEntity(it) }
            .collectList()
            .map { responseDto.withRoles(it) }
    }

    private fun assignSemesterRolesAsQL(
        ql: CourseSemesterRolesQL,
        appUser: AppUser,
        semesterId: Long
    ): Mono<CourseSemesterRolesQL> {
        return appUserCourseSemesterRoleRepository.findAllByAppUserIdEqualsAndCourseSemesterIdEquals(
            appUser.id!!,
            semesterId
        )
            .flatMap { courseSemesterRoleRepository.findById(it.courseSemesterRoleId) }
            .map { it.toQL() }
            .collectList()
            .doOnNext { ql.roles.addAll(it) }
            .map { ql }
    }

    fun findAllAsQL(): Flux<CourseSemesterRoleQL> {
        return courseSemesterRoleRepository.findAll()
            .map { it.toQL() }
    }
}