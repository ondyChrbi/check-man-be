package cz.fei.upce.checkman.domain.challenge

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("file_attachment")
data class FileAttachment(
    @Id var id: Long? = null,
    var name: String = "",
    var path: String = "",
    var available: Boolean = true,
    var creationDate: LocalDateTime = LocalDateTime.now(),
    var authorId: Long? = null
)
