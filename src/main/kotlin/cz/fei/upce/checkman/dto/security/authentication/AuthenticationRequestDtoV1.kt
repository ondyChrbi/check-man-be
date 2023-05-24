package cz.fei.upce.checkman.dto.security.authentication

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class AuthenticationRequestDtoV1(@field:NotNull @field:NotBlank var stagId : String = "")
