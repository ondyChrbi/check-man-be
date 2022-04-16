package cz.fei.upce.checkman.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("global_role")
data class GlobalRole(
    @Id var id: Long? = null,
    var name: String? = null
)
