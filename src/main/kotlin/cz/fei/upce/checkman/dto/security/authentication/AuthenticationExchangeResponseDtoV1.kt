package cz.fei.upce.checkman.dto.security.authentication

import java.time.LocalDateTime

data class AuthenticationExchangeResponseDtoV1(
    val code: String,
    val issueAt: LocalDateTime,
    val expiresAt: LocalDateTime
)
