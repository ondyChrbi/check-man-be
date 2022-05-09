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
@Operation(summary = "Start user authentication using Microsoft services.")
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
        responseCode = "406",
        description = "Email or user principals provided by Microsoft services is not part of university domain or there are corrupted. ",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    ),
    ApiResponse(
        responseCode = "500",
        description = "Error occur on server side. Please try it again later or contact technical support.",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    ),
    ApiResponse(
        responseCode = "503",
        description = "Third party service is not available. Please try it again later or contact technical support.",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    )
)
annotation class MicrosoftAuthenticationFinishEndpointV1
