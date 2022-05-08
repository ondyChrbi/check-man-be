package cz.fei.upce.checkman.service.role

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.repository.user.AppUserGlobalRoleRepository
import cz.fei.upce.checkman.repository.user.GlobalRoleRepository
import org.springframework.stereotype.Service

@Service
class GlobalRoleServiceV1(
    private val globalRoleRepository: GlobalRoleRepository,
    private val appUserGlobalRoleRepository: AppUserGlobalRoleRepository
) {
    fun rolesByUser(appUser: AppUser) = appUserGlobalRoleRepository.findAllByAppUserIdEquals(appUser.id!!)
        .flatMap { globalRoleRepository.findById(it.globalRoleId!!) }
}
