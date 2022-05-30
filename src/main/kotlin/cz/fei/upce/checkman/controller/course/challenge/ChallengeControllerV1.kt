package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.doc.course.challenge.*
import cz.fei.upce.checkman.doc.course.challenge.attachment.*
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.user.GlobalRole
import cz.fei.upce.checkman.dto.course.challenge.ChallengeRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.ChallengeResponseDtoV1
import cz.fei.upce.checkman.dto.course.challenge.PermitAppUserChallengeRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.RemoveAccessAppUserChallengeRequestDtoV1
import cz.fei.upce.checkman.dto.course.challenge.attachment.FileAttachmentResponseDtoV1
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import cz.fei.upce.checkman.service.course.CourseAccessRequest
import cz.fei.upce.checkman.service.course.CourseServiceV1
import cz.fei.upce.checkman.service.course.challenge.ChallengeServiceV1
import cz.fei.upce.checkman.service.course.challenge.attachment.ChallengeFileAttachmentServiceV1
import cz.fei.upce.checkman.service.course.challenge.ChallengeLocation
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
    private val challengeFileAttachmentService: ChallengeFileAttachmentServiceV1,
    private val courseService: CourseServiceV1,
    private val authenticationService: AuthenticationServiceV1
) {
    @GetMapping("")
    @PreAuthorize("""hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_VIEW}', 
        '${GlobalRole.ROLE_CHALLENGE_ACCESS}')""")
    @SearchChallengeEndpointV1
    fun search(
        @RequestParam(required = false, defaultValue = "") search: String?,
        @PathVariable courseId: Long,
        @PathVariable semesterId: Long,
        authentication: Authentication?
    ): Mono<ResponseEntity<CollectionModel<ChallengeResponseDtoV1>>> {
        val loggedUser = authenticationService.extractAuthenticateUser(authentication!!)
        val authorities = authenticationService.extractAuthorities(authentication)

        val accessRequest = CourseAccessRequest(courseId, semesterId, loggedUser, authorities)

        return courseService.checkCourseAccess(accessRequest, CourseSemesterRole.Value.COURSE_ROLE_ACCESS)
            .flatMapMany { challengeService.search(search, courseId, semesterId, loggedUser, authorities) }
            .flatMap { challengeFileAttachmentService.assignAll(it) }
            .flatMap { assignSelfRef(courseId, semesterId, it) }
            .collectList()
            .flatMap { assignSelfRef(courseId, semesterId, it) }
            .map { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    @PreAuthorize("""hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_VIEW}', 
        '${GlobalRole.ROLE_CHALLENGE_ACCESS}')""")
    @FindChallengeByIdEndpointV1
    fun find(
        @PathVariable id: Long,
        @PathVariable courseId: Long,
        @PathVariable semesterId: Long,
        authentication: Authentication?
    ): Mono<ResponseEntity<ChallengeResponseDtoV1>> {
        val loggedUser = authenticationService.extractAuthenticateUser(authentication!!)
        val authorities = authenticationService.extractAuthorities(authentication)

        val accessRequest = CourseAccessRequest(courseId, semesterId, loggedUser, authorities)

        return courseService.checkCourseAccess(accessRequest, CourseSemesterRole.Value.COURSE_ROLE_ACCESS)
            .flatMap { challengeService.find(id) }
            .map { ChallengeResponseDtoV1.fromEntity(it) }
            .flatMap { challengeFileAttachmentService.assignAll(it) }
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
        val loggedUser = authenticationService.extractAuthenticateUser(authentication)
        val authorities = authenticationService.extractAuthorities(authentication)

        val accessRequest = CourseAccessRequest(courseId, semesterId, loggedUser, authorities)

        return courseService.checkManageAccess(accessRequest, CourseSemesterRole.Value.COURSE_ROLE_CREATE_CHALLENGE)
            .flatMap { challengeService.add(courseId, semesterId, loggedUser, challengeDto.preventNullCollections()) }
            .flatMap { assignSelfRef(courseId, semesterId, it) }
            .map { ResponseEntity.ok(it) }
    }

    @PutMapping("/{challengeId}")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @UpdateChallengeEndpointV1
    fun edit(
        @Valid @RequestBody challengeDto: ChallengeRequestDtoV1, @PathVariable courseId: Long,
        @PathVariable semesterId: Long, @PathVariable challengeId: Long
    ): Mono<ResponseEntity<ChallengeResponseDtoV1>> {
        return challengeService.edit(courseId, semesterId, challengeId, challengeDto.preventNullCollections())
            .flatMap { assignSelfRef(courseId, semesterId, it) }
            .map { ResponseEntity.ok(it) }
    }

    @DeleteMapping("/{challengeId}")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @DeleteChallengeEndpointV1
    fun delete(
        @PathVariable courseId: Long, @PathVariable semesterId: Long, @PathVariable challengeId: Long
    ) = challengeService.delete(courseId, semesterId, challengeId).map { ResponseEntity.noContent().build<String>() }

    @GetMapping("/{challengeId}/attachment")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @SearchFileAttachmentEndpointV1
    fun search(
        @PathVariable courseId: Long, @PathVariable semesterId: Long, @PathVariable challengeId: Long,
        @RequestParam(required = false, defaultValue = "") search: String?
    ): Mono<ResponseEntity<CollectionModel<FileAttachmentResponseDtoV1>>> {
        val ids = ChallengeLocation(courseId, semesterId, challengeId)

        return challengeFileAttachmentService.findAll(ids, search)
            .flatMap { assignSelfRef(courseId, semesterId, challengeId, it) }
            .collectList()
            .flatMap { assignSelfRef(courseId, semesterId, challengeId, it) }
            .map { ResponseEntity.ok(it) }
    }

    @GetMapping("/{challengeId}/attachment/{attachmentId}")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @FindFileAttachmentByIdEndpointV1
    fun findFileAttachment(
        @PathVariable courseId: Long, @PathVariable semesterId: Long,
        @PathVariable challengeId: Long, @PathVariable attachmentId: Long
    ): Mono<ResponseEntity<FileAttachmentResponseDtoV1>> {
        val ids = ChallengeLocation(courseId, semesterId, challengeId)

        return challengeFileAttachmentService.find(ids, attachmentId)
            .flatMap { assignSelfRef(courseId, semesterId, challengeId, it) }
            .map { ResponseEntity.ok(it) }
    }

    @GetMapping("/{challengeId}/attachment/{attachmentId}/download")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @DownloadChallengeFileAttachmentV1
    fun download(
        @PathVariable courseId: Long, @PathVariable semesterId: Long, @PathVariable challengeId: Long,
        @PathVariable attachmentId: Long
    ): Mono<ResponseEntity<Resource>> {
        val ids = ChallengeLocation(courseId, semesterId, challengeId)

        return challengeFileAttachmentService.load(ids, attachmentId).map { ResponseEntity.ok(it) }
    }

    @PostMapping("/{challengeId}/attachment")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @UploadChallengeFileAttachmentV1
    fun upload(
        @PathVariable courseId: Long, @PathVariable semesterId: Long, @PathVariable challengeId: Long,
        @RequestPart file: Mono<FilePart>, authentication: Authentication
    ): Mono<ResponseEntity<FileAttachmentResponseDtoV1>> {
        val ids = ChallengeLocation(courseId, semesterId, challengeId)

        return challengeFileAttachmentService.save(ids, file, authentication)
            .flatMap { assignSelfRef(courseId, semesterId, challengeId, it) }
            .map { ResponseEntity.status(HttpStatus.ACCEPTED).body(it) }
    }

    @DeleteMapping("/{challengeId}/attachment/{attachmentId}")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @DeleteChallengeFileAttachmentV1
    fun removeAttachment(
        @PathVariable courseId: Long, @PathVariable semesterId: Long, @PathVariable challengeId: Long,
        @PathVariable attachmentId: Long
    ): Mono<ResponseEntity<String>> {
        val ids = ChallengeLocation(courseId, semesterId, challengeId)

        return challengeFileAttachmentService.remove(ids, attachmentId).map { ResponseEntity.noContent().build() }
    }

    @PutMapping("/{challengeId}/app-user/permit")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @PermitAccessAppUserChallengeEndpointV1
    fun permitAccessAppUser(
        @RequestBody permitDto: PermitAppUserChallengeRequestDtoV1,
        @PathVariable courseId: Long,
        @PathVariable semesterId: Long,
        @PathVariable challengeId: Long
    ): Mono<ResponseEntity<String>> {
        val location = ChallengeLocation(courseId, semesterId, challengeId)

        return challengeService.permitAccess(location, permitDto).map { ResponseEntity.noContent().build() }
    }

    @PutMapping("/{challengeId}/app-user/disable")
    @PreAuthorize("hasAnyRole('${GlobalRole.ROLE_COURSE_MANAGE}', '${GlobalRole.ROLE_COURSE_SEMESTER_MANAGE}', '${GlobalRole.ROLE_COURSE_CHALLENGE_MANAGE}')")
    @RemoveAccessAppUserChallengeEndpointV1
    fun removeAccessAppUser(
        @RequestBody removeDto: RemoveAccessAppUserChallengeRequestDtoV1,
        @PathVariable courseId: Long,
        @PathVariable semesterId: Long,
        @PathVariable challengeId: Long
    ): Mono<ResponseEntity<String>> {
        val location = ChallengeLocation(courseId, semesterId, challengeId)

        return challengeService.removeAccess(location, removeDto).then(Mono.just(ResponseEntity.noContent().build()))
    }

    @ExceptionHandler(java.nio.file.NoSuchFileException::class)
    fun handleRequestBodyError(ex: NoSuchFileException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.GONE).body("File already deleted")
    }

    private fun assignSelfRef(
        courseId: Long, semesterId: Long, challenge: ChallengeResponseDtoV1
    ): Mono<ChallengeResponseDtoV1> {
        return linkTo(methodOn(this::class.java).find(challenge.id!!, courseId, semesterId, null))
            .withSelfRel()
            .toMono()
            .map { challenge.add(it) }
    }

    private fun assignSelfRef(
        courseId: Long, semesterId: Long, challenges: Collection<ChallengeResponseDtoV1>
    ): Mono<CollectionModel<ChallengeResponseDtoV1>> {
        return linkTo(methodOn(this::class.java).search(null, courseId, semesterId, null))
            .withSelfRel()
            .toMono()
            .map { CollectionModel.of(challenges, it) }
    }

    private fun assignSelfRef(
        courseId: Long, semesterId: Long, challengeId: Long, fileAttachment: FileAttachmentResponseDtoV1
    ): Mono<FileAttachmentResponseDtoV1> {
        return linkTo(
            methodOn(this::class.java).findFileAttachment(
                challengeId,
                courseId,
                semesterId,
                fileAttachment.id!!
            )
        )
            .withSelfRel()
            .toMono()
            .map { fileAttachment.add(it) }
    }

    private fun assignSelfRef(
        courseId: Long, semesterId: Long, challengeId: Long, fileAttachments: Collection<FileAttachmentResponseDtoV1>
    ): Mono<CollectionModel<FileAttachmentResponseDtoV1>> {
        return linkTo(methodOn(this::class.java).search(courseId, semesterId, challengeId, null))
            .withSelfRel()
            .toMono()
            .map { CollectionModel.of(fileAttachments, it) }
    }
}