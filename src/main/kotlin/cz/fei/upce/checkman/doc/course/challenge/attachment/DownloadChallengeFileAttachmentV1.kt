package cz.fei.upce.checkman.doc.course.challenge.attachment

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.MediaType
import java.lang.annotation.Inherited

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@Operation(summary = "Download file linked with challenge.", security = [SecurityRequirement(name = "bearerAuth")])
@ApiResponses(
    ApiResponse(responseCode = "200", content = [Content(
        mediaType = "*/*",
        schema = Schema(implementation = Object::class)
    )]),
    ApiResponse(
        responseCode = "400",
        description = "Not valid request. Check all values.",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    ),
    ApiResponse(
        responseCode = "401",
        description = "Not authorized.",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    ),
    ApiResponse(
        responseCode = "403",
        description = "Missing permissions.",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    ),
    ApiResponse(
        responseCode = "500",
        description = "File upload failed ot error occur on server side. Please try it again later or contact technical support.",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    )
)
annotation class DownloadChallengeFileAttachmentV1
