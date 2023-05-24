package cz.fei.upce.checkman.dto.course.challenge.attachment

import cz.fei.upce.checkman.domain.challenge.FileAttachment
import cz.fei.upce.checkman.dto.ResponseDto
import java.time.LocalDateTime

data class FileAttachmentResponseDtoV1(
    var id: Long? = -1,
    var name: String = "",
    var available: Boolean = true,
    var creationDate: LocalDateTime = LocalDateTime.now(),
) : ResponseDto<FileAttachment, FileAttachmentResponseDtoV1>() {
    override fun withId(id: Long?): FileAttachmentResponseDtoV1 {
        this.id = id
        return this
    }

    override fun toEntity() = FileAttachment(
        name = name,
        available = available,
        creationDate = creationDate
    )

    override fun toEntity(entity: FileAttachment): FileAttachment {
        entity.name = name
        entity.available = available
        entity.creationDate = creationDate

        return entity
    }

    companion object {
        fun fromEntity(entity: FileAttachment) = FileAttachmentResponseDtoV1(
            id = entity.id,
            name = entity.name,
            available = entity.available,
            creationDate = entity.creationDate
        )
    }
}
