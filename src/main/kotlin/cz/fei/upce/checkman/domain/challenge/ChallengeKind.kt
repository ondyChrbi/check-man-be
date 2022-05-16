package cz.fei.upce.checkman.domain.challenge

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("challenge-kind")
data class ChallengeKind(
    @Id var id: Long? = null,
    var name: String? = null,
    val private: Boolean? = null
) {
    enum class Value(val id: Long, val private: Boolean, val obligatory: Boolean) {
        OPTIONAL(0, false, false),
        MANDATORY(1, false, true),
        CREDIT(2, true, true),
        EXAM(3, true, true);

        fun toEntity() = ChallengeKind(id, this.toString(), private)

        companion object {
            fun getById(id: Long) = values()[id.toInt()]
        }
    }
}
