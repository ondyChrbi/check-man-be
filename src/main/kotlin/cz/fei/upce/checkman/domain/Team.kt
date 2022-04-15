package cz.fei.upce.checkman.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("team")
data class Team(
    @Id var id : Long? = null,
    var name : String? = null,
    var creationDate : LocalDateTime? = null,
    var private : Boolean? = null,
    var minMembers : Int? = null,
    var user : AppUser? = null
)
