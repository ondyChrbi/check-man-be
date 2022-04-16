package cz.fei.upce.checkman.domain.challenge

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("challenge_file_attachment")
data class ChallengeFileAttachment(
    @Id var id: Long? = null,
    var challenge: Challenge? = null,
    var fileAttachment: FileAttachment? = null
)
