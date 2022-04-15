package cz.fei.upce.checkman.repository

import cz.fei.upce.checkman.domain.AppUserGlobalRole
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AppUserGlobalRoleRepository : ReactiveCrudRepository<AppUserGlobalRole, Long>