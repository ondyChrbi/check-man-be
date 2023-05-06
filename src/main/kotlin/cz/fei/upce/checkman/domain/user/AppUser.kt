package cz.fei.upce.checkman.domain.user

import cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL
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
) {
    fun toQL() = cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL(
        id!!,
        stagId,
        mail,
        displayName,
        registrationDate,
        lastAccessDate,
        disabled
    )
}
