package cz.fei.upce.checkman.service.authentication

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class ExchangeRequestDtoV1 (
    @field:NotNull(message = "{authentication.microsoft.exchange.code.not-null}")
    @field:NotBlank(message = "{authentication.microsoft.exchange.code.not-blank}")
    val authToken: String
)