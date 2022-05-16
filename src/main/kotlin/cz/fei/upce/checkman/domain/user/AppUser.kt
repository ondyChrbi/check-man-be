package cz.fei.upce.checkman.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("app_user")
data class AppUser(
    @Id var id: Long? = null,
    var stagId: String = "",
    var mail: String = "",
    var displayName: String = "",
    var registrationDate: LocalDateTime = LocalDateTime.now(),
    var lastAccessDate: LocalDateTime = LocalDateTime.now(),
    var disabled: Boolean = false
)
