package cz.fei.upce.checkman.service

import cz.fei.upce.checkman.repository.user.AppUserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceV1(private val appUserRepository: AppUserRepository) {

}
