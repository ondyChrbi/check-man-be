package cz.fei.upce.checkman.service.course.challenge.attachment

import cz.fei.upce.checkman.component.rsql.ReactiveCriteriaRsqlSpecification
import cz.fei.upce.checkman.domain.challenge.Challenge
import cz.fei.upce.checkman.domain.challenge.ChallengeFileAttachment
import cz.fei.upce.checkman.domain.challenge.FileAttachment
import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.course.challenge.ChallengeResponseDtoV1
import cz.fei.upce.checkman.dto.course.challenge.attachment.FileAttachmentResponseDtoV1
import cz.fei.upce.checkman.repository.challenge.ChallengeFileAttachmentRepository
import cz.fei.upce.checkman.repository.challenge.FileAttachmentRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import cz.fei.upce.checkman.service.course.challenge.ChallengeLocation
import cz.fei.upce.checkman.service.course.challenge.ChallengeServiceV1
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import javax.annotation.PostConstruct
import kotlin.io.path.absolutePathString

@Service
class ChallengeFileAttachmentServiceV1(
    private val challengeService: ChallengeServiceV1,
    private val challengeFileAttachmentRepository: ChallengeFileAttachmentRepository,
    private val fileAttachmentRepository: FileAttachmentRepository,
    private val entityTemplate: R2dbcEntityTemplate,
    private val reactiveCriteriaRsqlSpecification: ReactiveCriteriaRsqlSpecification,
    private val authenticationService: AuthenticationServiceV1
) {
    @Value("\${check-man.challenge.file-attachment.path}")
    private var baseLocation: String = "./"

    private lateinit var basePath: Path

    @PostConstruct
    fun init() {
        this.basePath = Paths.get(baseLocation)
    }

    fun assignAll(challengeResponseDto: ChallengeResponseDtoV1): Mono<ChallengeResponseDtoV1> {
        return findAll(challengeResponseDto.id!!)
            .collectList()
            .map { challengeResponseDto.withAttachments(it) }
    }

    fun findAll(challengeId: Long): Flux<FileAttachmentResponseDtoV1> {
        return challengeFileAttachmentRepository.findAllByChallengeIdEquals(challengeId)
            .flatMap { fileAttachmentRepository.findById(it.fileAttachmentId) }
            .map { FileAttachmentResponseDtoV1.fromEntity(it) }
    }

    fun findAll(location: ChallengeLocation, search: String?): Flux<FileAttachmentResponseDtoV1> {
        val condition = where("challengeId").`is`(location.challengeId)

        val fileAttachments = if (search == null || search.isEmpty())
            this.findAll(location.challengeId)
        else
            entityTemplate.select(FileAttachment::class.java)
                .matching(reactiveCriteriaRsqlSpecification.createCriteria(search, condition))
                .all()
                .map { FileAttachmentResponseDtoV1.fromEntity(it) }

        return challengeService.checkChallengeAssociation(location)
            .flatMapMany{ fileAttachments }
    }

    fun find(ids: ChallengeLocation, attachmentId: Long): Mono<FileAttachmentResponseDtoV1> {
        return challengeService.checkChallengeAssociation(ids)
            .flatMap { fileAttachmentRepository.findById(attachmentId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { FileAttachmentResponseDtoV1.fromEntity(it) }
    }

    fun load(ids: ChallengeLocation, attachmentId: Long): Mono<FileSystemResource> {
        return checkChallengeAssociation(ids, attachmentId)
            .flatMap { fileAttachmentRepository.findById(attachmentId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { attachment -> Mono.fromCallable { FileSystemResource(Paths.get(attachment.path)) } }
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun save(ids: ChallengeLocation, filePartMono: Mono<FilePart>, authentication: Authentication): Mono<FileAttachmentResponseDtoV1> {
        return filePartMono.flatMap { filePart ->
            toFileProperties(ids, createFileAlias(filePart), authentication)
                .flatMap { properties -> save(properties, filePart) }
        }
    }

    private fun save(properties: FileAttachmentProperties, filePart: FilePart): Mono<FileAttachmentResponseDtoV1> {
        return saveFileAttachment(filePart, properties, properties.file)
            .flatMap { attachment ->
                linkFileAttachmentToChallenge(properties, attachment)
                    .flatMap { createDirectoryStructureIfNotExists(properties) }
                    .flatMap { filePart.transferTo(properties.saveDestination.resolve(properties.file)) }
                    .then(Mono.just(FileAttachmentResponseDtoV1.fromEntity(attachment)))
            }
    }

    fun remove(ids: ChallengeLocation, attachmentId: Long): Mono<Void> {
        return challengeService.checkChallengeAssociation(ids)
            .flatMap { fileAttachmentRepository.findById(attachmentId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { deleteFile(it) }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap { challengeFileAttachmentRepository.deleteAllByFileAttachmentIdEquals(attachmentId) }
            .then(fileAttachmentRepository.deleteById(attachmentId))
    }

    private fun toFileProperties(ids: ChallengeLocation, file: Path, authentication: Authentication): Mono<FileAttachmentProperties> {
        val appUser = authenticationService.extractAuthenticateUser(authentication)
        val destination = createDestinationStructure(ids)

        return challengeService.findCourseSemester(ids.semesterId, ids.courseId)
            .flatMap { courseSemester ->
                challengeService.find(ids.challengeId)
                    .map { challenge ->
                        FileAttachmentProperties(courseSemester, challenge, appUser, destination, file)
                    }
            }
    }

    private fun linkFileAttachmentToChallenge(properties: FileAttachmentProperties, attachment: FileAttachment): Mono<ChallengeFileAttachment> {
        return challengeFileAttachmentRepository.save(
            ChallengeFileAttachment(
                challengeId = properties.challenge.id!!,
                fileAttachmentId = attachment.id!!
            )
        )
    }

    private fun saveFileAttachment(
        partFile: FilePart, properties: FileAttachmentProperties, fileName: Path
    ): Mono<FileAttachment> {
        return fileAttachmentRepository.save(
            FileAttachment(
                name = partFile.name(),
                path = properties.saveDestination.resolve(fileName).absolutePathString(),
                authorId = properties.author.id
            )
        )
    }

    private fun createDirectoryStructureIfNotExists(properties: FileAttachmentProperties) = Mono.fromCallable {
        if (Files.notExists(properties.saveDestination)) {
            Files.createDirectories(properties.saveDestination)
        }
    }

    private fun deleteFile(attachment: FileAttachment) = Mono.fromCallable {
        Files.delete(Paths.get(attachment.path))
    }

    private fun createFileAlias(multipartFile: FilePart): Path {
        val filename = multipartFile.filename()
        val extension = if (filename.contains(".")) ".${filename.substringAfter(".")}" else ""

        return Paths.get(UUID.randomUUID().toString() + extension)
    }

    private fun createDestinationStructure(properties: ChallengeLocation): Path {
        val coursePath = Paths.get("course/${properties.courseId}")
        val semesterPath = Paths.get("semester/${properties.semesterId}")
        val challengePath = Paths.get("challenge/${properties.challengeId}")

        return basePath.resolve(coursePath).resolve(semesterPath).resolve(challengePath)
    }

    private fun checkChallengeAssociation(ids: ChallengeLocation, attachmentId: Long): Mono<Boolean> {
        return challengeService.checkChallengeAssociation(ids)
            .flatMap { challengeFileAttachmentRepository.existsByChallengeIdEqualsAndFileAttachmentIdEquals(ids.challengeId, attachmentId) }
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap {
                if (!it)
                    Mono.error(ResourceNotFoundException())
                else
                    Mono.just(it)
            }
    }

    data class FileAttachmentProperties(
        val courseSemester: CourseSemester,
        val challenge: Challenge,
        val author: AppUser,
        val saveDestination: Path,
        val file: Path
    )
}