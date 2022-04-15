package cz.fei.upce.checkman.repository

import cz.fei.upce.checkman.domain.GlobalRole
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GlobalRoleRepository : ReactiveCrudRepository<Long, GlobalRole>