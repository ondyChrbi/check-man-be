package cz.fei.upce.checkman.domain.user

import cz.fei.upce.checkman.component.security.JwtTokenInfo
import java.util.UUID

data class AuthenticationExchange (
    var id: String = UUID.randomUUID().toString(),
    var jwtTokenInfo: JwtTokenInfo
) {
    fun getRedisKey() = AuthenticationRequest.getRedisKey(id)
}