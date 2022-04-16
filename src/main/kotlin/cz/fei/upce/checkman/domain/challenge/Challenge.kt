package cz.fei.upce.checkman.domain.challenge

import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.domain.user.AppUser
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("challenge")
data class Challenge(
    @Id var id: Long? = null,
    var name: String? = null,
    var description: String? = null,
    var deadlineDate: LocalDateTime? = null,
    var startDate: LocalDateTime? = null,
    var obligatory: Boolean? = null,
    var author: AppUser? = null,
    var courseSemester: CourseSemester? = null,
    var challengeKind: ChallengeKind? = null
) {
    enum class ChallengeKind(private: Boolean) {
        OPTIONAL(false),
        MANDATORY(false),
        CREDIT(true),
        EXAM(true),
    }
}
