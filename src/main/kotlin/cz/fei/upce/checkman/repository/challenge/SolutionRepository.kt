package cz.fei.upce.checkman.repository.challenge

import cz.fei.upce.checkman.domain.challenge.Solution
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SolutionRepository : ReactiveCrudRepository<Solution, Long>