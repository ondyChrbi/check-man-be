package cz.fei.upce.checkman.doc.authentication.microsoft

import cz.fei.upce.checkman.dto.security.authentication.AuthenticationResponseDtoV1
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.MediaType
import java.lang.annotation.Inherited

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@Operation(summary = "Exchange code to JWT authentication token.")
@ApiResponses(
    ApiResponse(
        responseCode = "200",
        description = "JWT token.",
        content = [Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = Schema(implementation = AuthenticationResponseDtoV1::class)
        )]
    ),
    ApiResponse(
        responseCode = "400",
        description = "Not valid request. Check all values.",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    ),
    ApiResponse(
        responseCode = "404",
        description = "Record not found.",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    ),
    ApiResponse(
        responseCode = "500",
        description = "Error occur on server side. Please try it again later or contact technical support.",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    )
)
annotation class MicrosoftAuthenticationExchangeEndpointV1
