package cz.fei.upce.checkman.domain.challenge

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("file_attachment")
data class FileAttachment(
    @Id var id : Long? = null,
    var name : String? = null,
    var path : String? = null,
    var available : Boolean? = null,
    var creation_date : LocalDateTime? = null,
    var challenge: Challenge? = null
)
