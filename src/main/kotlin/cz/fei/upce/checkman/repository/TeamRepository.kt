package cz.fei.upce.checkman.repository

import cz.fei.upce.checkman.domain.Team
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRepository : ReactiveCrudRepository<Team, Long>