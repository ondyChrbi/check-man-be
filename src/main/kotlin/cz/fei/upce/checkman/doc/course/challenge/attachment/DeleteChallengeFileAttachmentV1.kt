package cz.fei.upce.checkman.doc.course.challenge.attachment

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.MediaType
import java.lang.annotation.Inherited

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@Operation(summary = "Delete file attachment.", security = [SecurityRequirement(name = "bearerAuth")])
@ApiResponses(
    ApiResponse(
        responseCode = "204",
        description = "Success.",
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
        responseCode = "404",
        description = "File attachment not found.",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    ),
    ApiResponse(
        responseCode = "410",
        description = "File already deleted.",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    ),
    ApiResponse(
        responseCode = "500",
        description = "Error occur on server side. Please try it again later or contact technical support.",
        content = [Content(mediaType = MediaType.TEXT_PLAIN_VALUE)]
    )
)
annotation class DeleteChallengeFileAttachmentV1
