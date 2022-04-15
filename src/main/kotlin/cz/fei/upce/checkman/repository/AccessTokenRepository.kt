package cz.fei.upce.checkman.repository

import cz.fei.upce.checkman.domain.AccessToken
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccessTokenRepository : ReactiveCrudRepository<AccessToken, Long>