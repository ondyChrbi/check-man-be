package cz.fei.upce.checkman.dto.security

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class AuthenticationRequestDtoV1(@field:NotNull @field:NotBlank var email : String = "")
