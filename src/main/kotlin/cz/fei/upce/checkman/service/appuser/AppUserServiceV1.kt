package cz.fei.upce.checkman.service.appuser

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.appuser.AppUserResponseDtoV1
import cz.fei.upce.checkman.dto.appuser.GlobalRoleResponseDtoV1
import cz.fei.upce.checkman.dto.security.authentication.AuthenticationRequestDtoV1
import cz.fei.upce.checkman.repository.user.AppUserRepository
import cz.fei.upce.checkman.service.role.CourseSemesterRoleServiceV1
import cz.fei.upce.checkman.service.role.GlobalRoleServiceV1
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class AppUserServiceV1(
    private val appUserRepository: AppUserRepository,
    private val teamService: TeamServiceV1,
    private val globalRoleService: GlobalRoleServiceV1,
    private val courseSemesterRoleService: CourseSemesterRoleServiceV1
) {
    fun findUser(authenticationRequest: AuthenticationRequestDtoV1) = findUser(authenticationRequest.stagId)

    fun findUser(stagId: String) = appUserRepository.findByStagIdEquals(stagId)

    fun updateLastAccessDate(appUser: AppUser): Mono<AppUser> {
        appUser.lastAccessDate = LocalDateTime.now()
        return appUserRepository.save(appUser)
    }

    fun updateLastAccessDate(stagId: String): Mono<AppUser> {
        return appUserRepository.findByStagIdEquals(stagId)
            .flatMap(this::updateLastAccessDate)
    }

    fun save(appUser: AppUser) = appUserRepository.save(appUser)
        .flatMap(teamService::createPersonalTeam)
        .flatMap { appUserRepository.findById(appUser.id!!) }

    fun me(loggedUser: AppUser): Mono<AppUserResponseDtoV1> {
        val responseDto = AppUserResponseDtoV1.fromEntity(loggedUser)

        return globalRoleService.rolesByUser(loggedUser)
            .map { GlobalRoleResponseDtoV1.fromEntity(it) }
            .collectList()
            .doOnNext { responseDto.withGlobalRoles(it) }
            .flatMapMany { courseSemesterRoleService.findAllSemestersAndRoles(loggedUser) }
            .collectList()
            .map { responseDto.withCourseRoles(it) }
    }
}
