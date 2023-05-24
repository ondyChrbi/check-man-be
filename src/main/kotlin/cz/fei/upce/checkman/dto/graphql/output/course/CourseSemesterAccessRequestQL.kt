package cz.fei.upce.checkman.dto.graphql.output.course

import cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL
import java.time.LocalDateTime

data class CourseSemesterAccessRequestQL(
    val appUser: AppUserQL,
    val semesterId: Long,
    val creationDate: LocalDateTime,
    val expirationDate: LocalDateTime,
    val id: String
)