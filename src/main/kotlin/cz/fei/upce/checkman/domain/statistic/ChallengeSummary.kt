package cz.fei.upce.checkman.domain.statistic

import cz.fei.upce.checkman.dto.course.challenge.ChallengeSummaryQL
import org.springframework.data.relational.core.mapping.Table

@Table(name = "challenge_summary")
data class ChallengeSummary(
    var courseId: Long,
    var challengeId: Long,
    var statusId: Long,
    var statusName: String,
    var appUserId: Long,
    var count: Long
) {
    fun toQL(): ChallengeSummaryQL {
        return ChallengeSummaryQL(
            courseId = courseId,
            challengeId = challengeId,
            statusId = statusId,
            statusName = statusName,
            appUserId = appUserId,
            count = count
        )
    }
}
