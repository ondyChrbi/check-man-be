package cz.fei.upce.checkman.repository.challenge

import cz.fei.upce.checkman.domain.challenge.Challenge
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ChallengeRepository : ReactiveCrudRepository<Challenge, Long>