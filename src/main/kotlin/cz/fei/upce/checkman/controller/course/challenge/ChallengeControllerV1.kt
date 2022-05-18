package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.doc.course.challenge.SearchChallengeEndpointV1
import cz.fei.upce.checkman.doc.course.challenge.FindChallengeByIdEndpointV1
import cz.fei.upce.checkman.doc.course.challenge.CreateChallengeEndpointV1
import cz.fei.upce.checkman.doc.course.challenge.DeleteChallengeEndpointV1
import cz.fei.upce.checkman.doc.course.challenge.UpdateChallengeEndpointV1
import cz.fei.upce.checkman.doc.course.challenge.attachment.DownloadChallengeFileAttachmentV1
import cz.fei.upce.checkman.doc.course.challenge.attachment.UploadChallengeFileAttachmentV1
import cz.fei.upce.checkman.domain.user.GlobalRole
import cz.fei.upce.checkman.dto.course.challenge.ChallengeRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.ChallengeResponseDtoV1
import cz.fei.upce.checkman.dto.course.challenge.attachment.FileAttachmentResponseDtoV1
import cz.fei.upce.checkman.service.course.challenge.ChallengeServiceV1
import cz.fei.upce.checkman.service.course.challenge.attachment.ChallengeFileAttachmentServiceV1
import cz.fei.upce.checkman.service.course.challenge.attachment.ChallengeFileAttachmentServiceV1.FileAttachmentIds
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.Resource
import org.springframework.hateoas.CollectionModel
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/v1/course/{courseId}/semester/{semesterId}/challenge")
@Tag(name = "Challenge V1", description = "Challenge API (V1)")
class ChallengeControllerV1(
    private val challengeService: ChallengeServiceV1,
    private val challengeFileAttachmentService: ChallengeFileAttachmentServiceV1
) {
    @GetMapping("")
    @PreAuthorize(
        """
        hasAnyRole(
            '${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', 
            '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_VIEW}',
            ''
        )"""
    )
    @SearchChallengeEndpointV1
    fun search(
        @RequestParam(required = false, defaultValue = "") search: String?,
        @PathVariable courseId: Long,
        @PathVariable semesterId: Long
    ): Mono<ResponseEntity<CollectionModel<ChallengeResponseDtoV1>>> {
        return challengeService.search(search, courseId, semesterId)
            .flatMap { assignSelfRef(courseId, semesterId, it) }
            .collectList()
            .flatMap { assignSelfRef(courseId, semesterId, it) }
            .map { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    @PreAuthorize(
        """
        hasAnyRole(
            '${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', 
            '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_VIEW}',
            ''
        )"""
    )
    @FindChallengeByIdEndpointV1
    fun find(
        @PathVariable id: Long,
        @PathVariable courseId: Long,
        @PathVariable semesterId: Long
    ): Mono<ResponseEntity<ChallengeResponseDtoV1>> {
        return challengeService.find(id)
            .map { ChallengeResponseDtoV1.fromEntity(it) }
            .flatMap { assignSelfRef(courseId, semesterId, it) }
            .map { ResponseEntity.ok(it) }
    }

    @PostMapping("")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @CreateChallengeEndpointV1
    fun add(
        @Valid @RequestBody challengeDto: ChallengeRequestDtoV1, @PathVariable courseId: Long,
        @PathVariable semesterId: Long, authentication: Authentication
    ): Mono<ResponseEntity<ChallengeResponseDtoV1>> {
        return challengeService.add(challengeDto.preventNullCollections(), courseId, semesterId, authentication)
            .map { ResponseEntity.ok(it) }
    }

    @PutMapping("/{challengeId}")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @UpdateChallengeEndpointV1
    fun edit(
        @Valid @RequestBody challengeDto: ChallengeRequestDtoV1, @PathVariable courseId: Long,
        @PathVariable semesterId: Long, @PathVariable challengeId: Long
    ): Mono<ResponseEntity<ChallengeResponseDtoV1>> {
        return challengeService.edit(challengeDto.preventNullCollections(), courseId, semesterId, challengeId)
            .map { ResponseEntity.ok(it) }
    }

    @DeleteMapping("/{challengeId}")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @DeleteChallengeEndpointV1
    fun delete(
        @PathVariable courseId: Long, @PathVariable semesterId: Long, @PathVariable challengeId: Long
    ) = challengeService.delete(courseId, semesterId, challengeId).map { ResponseEntity.noContent().build<String>() }

    @GetMapping("/{challengeId}/attachment/{attachmentId}")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @DownloadChallengeFileAttachmentV1
    fun download(@PathVariable courseId: Long, @PathVariable semesterId: Long, @PathVariable challengeId: Long,
                 @PathVariable attachmentId: Long): Mono<ResponseEntity<Resource>> {
        val ids = FileAttachmentIds(courseId, semesterId, challengeId)

        return challengeFileAttachmentService.load(ids, attachmentId).map { ResponseEntity.ok(it) }
    }

    @PostMapping("/{challengeId}/attachment")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @UploadChallengeFileAttachmentV1
    fun upload(
        @PathVariable courseId: Long, @PathVariable semesterId: Long, @PathVariable challengeId: Long,
        @RequestPart file: Mono<FilePart>, authentication: Authentication
    ): Mono<ResponseEntity<FileAttachmentResponseDtoV1>> {
        val ids = FileAttachmentIds(courseId, semesterId, challengeId)

        return challengeFileAttachmentService.save(ids, file, authentication).map { ResponseEntity.status(HttpStatus.ACCEPTED).body(it) }
    }

    private fun assignSelfRef(
        courseId: Long,
        semesterId: Long,
        challenge: ChallengeResponseDtoV1
    ): Mono<ChallengeResponseDtoV1> {
        return linkTo(methodOn(this::class.java).find(challenge.id!!, courseId, semesterId))
            .withSelfRel()
            .toMono()
            .map { challenge.add(it) }
    }

    private fun assignSelfRef(
        courseId: Long,
        semesterId: Long,
        challenges: Collection<ChallengeResponseDtoV1>
    ): Mono<CollectionModel<ChallengeResponseDtoV1>> {
        return linkTo(methodOn(this::class.java).search(null, courseId, semesterId))
            .withSelfRel()
            .toMono()
            .map { CollectionModel.of(challenges, it) }
    }
}