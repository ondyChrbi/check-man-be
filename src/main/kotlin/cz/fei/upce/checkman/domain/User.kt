package cz.fei.upce.checkman.domain

import java.util.*

data class User(
    var id : Long = 0,
    var stagId : String = "",
    var registrationDate : Date = Date(),
    var lastAccessDate : Date = Date(),
    var disabled : Boolean = false
)
