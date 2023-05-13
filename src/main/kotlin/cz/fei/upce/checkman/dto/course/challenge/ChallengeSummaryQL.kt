package cz.fei.upce.checkman.dto.course.challenge

data class ChallengeSummaryQL(
    var courseId: Long,
    var challengeId: Long,
    var statusId: Long,
    var statusName: String,
    var appUserId: Long,
    var count: Long
)
