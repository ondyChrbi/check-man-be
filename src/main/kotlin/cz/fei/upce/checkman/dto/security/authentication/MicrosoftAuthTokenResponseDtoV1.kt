package cz.fei.upce.checkman.dto.security.authentication

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class MicrosoftAuthTokenResponseDtoV1 (
    val tokenType : String? = null,
    val scope : String? = null,
    val expiresIn : Long? = null,
    val extExpiresIn : Long? = null,
    val accessToken : String? = null,
    val idToken : String? = null
)