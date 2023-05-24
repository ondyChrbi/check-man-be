package cz.fei.upce.checkman.dto.security.authentication

import cz.fei.upce.checkman.dto.appuser.AppUserResponseDtoV1
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class AuthenticationResponseDtoV1(
    @field:NotNull @field:NotBlank val token: String,
    val issueAtDate: LocalDateTime,
    val expireAtDate: LocalDateTime,
    val me: AppUserResponseDtoV1
)
