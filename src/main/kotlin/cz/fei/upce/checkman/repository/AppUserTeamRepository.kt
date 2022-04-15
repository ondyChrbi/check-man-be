package cz.fei.upce.checkman.repository

import cz.fei.upce.checkman.domain.AppUserTeam
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AppUserTeamRepository : ReactiveCrudRepository<AppUserTeam, Long>