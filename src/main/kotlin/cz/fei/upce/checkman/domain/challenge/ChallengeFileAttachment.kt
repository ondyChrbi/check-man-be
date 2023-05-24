package cz.fei.upce.checkman.domain.challenge

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("challenge_file_attachment")
data class ChallengeFileAttachment(
    @Id var id: Long? = null,
    var challengeId: Long = -1,
    var fileAttachmentId: Long = -1
)
