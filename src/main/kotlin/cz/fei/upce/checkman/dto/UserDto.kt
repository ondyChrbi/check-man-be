package cz.fei.upce.checkman.dto

import java.util.*

data class UserDto(
    var id : Long = 0L,
    var stagId : String? = null,
    var registrationDate : Date? = null,
    var lastAccessDate : Date? = null,
    var disabled : Boolean? = null
)
