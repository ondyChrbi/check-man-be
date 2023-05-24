package cz.fei.upce.checkman.dto.course.challenge

import java.time.LocalDateTime

data class PermitAppUserChallengeRequestDtoV1 (
    val appUserId: Long,
    val accessTo: LocalDateTime
)