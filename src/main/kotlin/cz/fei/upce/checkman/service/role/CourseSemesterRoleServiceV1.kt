package cz.fei.upce.checkman.service.role

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.appuser.CourseSemesterRoleDtoV1
import cz.fei.upce.checkman.dto.appuser.CourseSemesterRolesResponseDtoV1
import cz.fei.upce.checkman.dto.course.CourseSemesterResponseDtoV1
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

    fun findAllSemestersAndRoles(appUser: AppUser): Flux<CourseSemesterRolesResponseDtoV1> {
        return appUserCourseSemesterRoleRepository.findOnlySemestersByAppUserIEquals(appUser.id!!)
            .flatMap { findSemesterAndRoles(appUser, it.courseSemesterId) }
    }

    fun findSemesterAndRoles(appUser: AppUser, semesterId: Long): Mono<CourseSemesterRolesResponseDtoV1> {
        return courseSemesterRepository.findById(semesterId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { CourseSemesterRolesResponseDtoV1(CourseSemesterResponseDtoV1.fromEntity(it)) }
            .flatMap { assignSemesterRoles(it, appUser) }
    }

    private fun assignSemesterRoles(responseDto: CourseSemesterRolesResponseDtoV1, appUser: AppUser): Mono<CourseSemesterRolesResponseDtoV1> {
        return assignSemesterRoles(responseDto, appUser, responseDto.semester.id!!)
    }

    private fun assignSemesterRoles(responseDto: CourseSemesterRolesResponseDtoV1, appUser: AppUser, semesterId: Long): Mono<CourseSemesterRolesResponseDtoV1> {
        return appUserCourseSemesterRoleRepository.findAllByAppUserIdEqualsAndCourseSemesterIdEquals(appUser.id!!, semesterId)
            .flatMap { courseSemesterRoleRepository.findById(it.courseSemesterRoleId) }
            .map { CourseSemesterRoleDtoV1.fromEntity(it) }
            .collectList()
            .map { responseDto.withRoles(it) }
    }
}