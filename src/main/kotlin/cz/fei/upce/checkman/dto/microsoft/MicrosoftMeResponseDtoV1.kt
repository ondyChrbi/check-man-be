package cz.fei.upce.checkman.dto.microsoft

import com.fasterxml.jackson.annotation.JsonProperty

data class MicrosoftMeResponseDtoV1(
    @field:JsonProperty("@odata.context")
    val oDataContext : String? = null,
    val businessPhones : Collection<String>? = emptyList(),
    val displayName : String? = null,
    val jobTitle : String? = null,
    val mail : String? = null,
    val mobilePhone : String? = null,
    val officeLocation : String? = null,
    val preferredLanguage : String? = null,
    val userPrincipalName : String? = null,
)
