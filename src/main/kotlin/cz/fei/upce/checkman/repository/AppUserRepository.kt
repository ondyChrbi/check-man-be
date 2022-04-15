package cz.fei.upce.checkman.repository

import cz.fei.upce.checkman.domain.AppUser
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AppUserRepository : ReactiveCrudRepository<AppUser, Long>