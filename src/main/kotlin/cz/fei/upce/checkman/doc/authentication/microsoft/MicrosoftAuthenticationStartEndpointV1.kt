package cz.fei.upce.checkman.doc.authentication.microsoft

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
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
        responseCode = "303",
        description = "Redirection to Microsoft auth page where user will continue with authentication.",
        headers = [Header(
            name = "Location",
            description = "Microsoft authentication page redirect url.",
            schema = Schema(type = "url")
        )]
    ),
    ApiResponse(
        responseCode = "500",
        description = "Error occur on server side. Please try it again later or contact technical support.",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    )
)
annotation class MicrosoftAuthenticationStartEndpointV1
