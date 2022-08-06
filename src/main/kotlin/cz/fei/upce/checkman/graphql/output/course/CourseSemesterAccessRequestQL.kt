package cz.fei.upce.checkman.graphql.output.course

import cz.fei.upce.checkman.graphql.output.appuser.AppUserQL
import java.time.LocalDateTime

data class CourseSemesterAccessRequestQL(
    val appUser: AppUserQL,
    val courseSemester: CourseSemesterQL,
    val creationDate: LocalDateTime,
    val expirationDate: LocalDateTime,
    val id: String
)