package cz.fei.upce.checkman.repository.user

import cz.fei.upce.checkman.domain.user.GlobalRole
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface GlobalRoleRepository : ReactiveCrudRepository<GlobalRole, Long>