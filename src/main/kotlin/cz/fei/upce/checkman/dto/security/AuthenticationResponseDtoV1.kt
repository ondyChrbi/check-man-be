package cz.fei.upce.checkman.dto.security

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class AuthenticationResponseDtoV1(@field:NotNull @field:NotBlank val token : String)
