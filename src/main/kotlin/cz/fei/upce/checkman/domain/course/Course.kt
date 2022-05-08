package cz.fei.upce.checkman.domain.course

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("course")
data class Course(
    @Id var id: Long? = null,
    var stagId: String? = null,
    var name: String? = null,
    var dateCreation: LocalDateTime? = LocalDateTime.now(),
    var icon: String? = null,
    var template: String? = null
)