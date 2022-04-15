package cz.fei.upce.checkman.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("app_user")
data class AppUser(
    @Id var id : Long? = null,
    var stagId : String? = null,
    var registrationDate : LocalDateTime? = null,
    var lastAccessDate : LocalDateTime? = null,
    var disabled : Boolean? = null
)
