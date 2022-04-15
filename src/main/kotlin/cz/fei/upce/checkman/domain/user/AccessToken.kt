package cz.fei.upce.checkman.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("access_token")
data class AccessToken (
    @Id var id : Long? = null,
    var value : String? = null,
    var creationDate : LocalDateTime? = null,
    var validTo : LocalDateTime? = null,
    var disabled : Boolean? = null,
    var appUser : AppUser? = null
)