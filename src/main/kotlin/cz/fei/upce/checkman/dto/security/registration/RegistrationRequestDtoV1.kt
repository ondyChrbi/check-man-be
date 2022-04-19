package cz.fei.upce.checkman.dto.security.registration

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class RegistrationRequestDtoV1(@field:NotNull @field:NotBlank var stagId: String? = null)
