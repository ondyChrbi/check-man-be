package cz.fei.upce.checkman.component.security

import cz.fei.upce.checkman.dto.appuser.AppUserResponseDtoV1
import cz.fei.upce.checkman.dto.security.authentication.AuthenticationResponseDtoV1
import java.time.ZoneId
import java.util.*

data class JwtTokenInfo(
    val jwtToken: String,
    val issueAtDate: Date,
    val expireAtDate: Date
) {
    fun toAuthenticationResponseDtoV1 (appUserResponse: AppUserResponseDtoV1): AuthenticationResponseDtoV1 {
        return AuthenticationResponseDtoV1(
            jwtToken,
            issueAtDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
            expireAtDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
            appUserResponse
        )
    }
}
