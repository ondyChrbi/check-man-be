package cz.fei.upce.checkman.service.role

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.domain.user.AppUserGlobalRole
import cz.fei.upce.checkman.domain.user.GlobalRole
import cz.fei.upce.checkman.dto.role.global.AppUserGlobalRoleDtoV1
import cz.fei.upce.checkman.repository.user.AppUserGlobalRoleRepository
import cz.fei.upce.checkman.repository.user.AppUserRepository
import cz.fei.upce.checkman.repository.user.GlobalRoleRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class GlobalRoleServiceV1(
    private val globalRoleRepository: GlobalRoleRepository,
    private val appUserGlobalRoleRepository: AppUserGlobalRoleRepository,
    private val appUserRepository: AppUserRepository
) {
    fun rolesByUser(appUser: AppUser) = appUserGlobalRoleRepository.findAllByAppUserIdEquals(appUser.id!!)
        .flatMap { globalRoleRepository.findById(it.globalRoleId!!) }

    fun assign(appUserGlobalRoleDto: AppUserGlobalRoleDtoV1): Mono<AppUserGlobalRoleDtoV1> {
        return assign(appUserGlobalRoleDto.appUserId!!, appUserGlobalRoleDto.globalRoleId!!).map {
            appUserGlobalRoleDto.withId(null)
        }
    }

    fun remove(appUserGlobalRoleDto: AppUserGlobalRoleDtoV1): Mono<AppUserGlobalRoleDtoV1> {
        return appUserGlobalRoleRepository.existsByGlobalRoleIdEqualsAndAppUserIdEquals(
            appUserGlobalRoleDto.globalRoleId!!,
            appUserGlobalRoleDto.appUserId!!
        ).switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap {
                appUserGlobalRoleRepository.deleteAllByGlobalRoleIdEqualsAndAppUserIdEquals(
                    appUserGlobalRoleDto.globalRoleId!!,
                    appUserGlobalRoleDto.appUserId!!
                )
            }.map { appUserGlobalRoleDto.withId(null) }
    }

    private fun assign(appUserId: Long, globalRoleId: Long): Mono<AppUserGlobalRole> {
        return appUserRepository.findById(appUserId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { assign(it, globalRoleId) }
    }

    private fun assign(appUser: AppUser, globalRoleId: Long): Mono<AppUserGlobalRole> {
        return globalRoleRepository.findById(globalRoleId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { assign(appUser, it) }
    }

    fun assign(appUser: AppUser, globalRole: GlobalRole): Mono<AppUserGlobalRole> {
        return appUserGlobalRoleRepository.existsByGlobalRoleIdEqualsAndAppUserIdEquals(globalRole.id!!, appUser.id!!)
            .flatMap {
                if (!it) {
                    appUserGlobalRoleRepository.save(
                        AppUserGlobalRole(
                            appUserId = appUser.id,
                            globalRoleId = globalRole.id
                        )
                    )
                } else {
                    Mono.error(RoleAlreadyAssignedException(appUser.id!!, globalRole.id!!))
                }
            }
    }
}
