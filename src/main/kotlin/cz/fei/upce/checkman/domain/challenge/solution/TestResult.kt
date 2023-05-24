package cz.fei.upce.checkman.domain.challenge.solution

import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestResultQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.ZoneOffset

@Table("test_result")
data class TestResult(
    @field:Id var id: Long? = null,
    var log: String = "",
    var creationDate: LocalDateTime = LocalDateTime.now(),
    var updateDate: LocalDateTime? = null,
    var testStatusId: Long? = TestStatus.WAITING_TO_TEST.id,
    var solutionId: Long? = 0L,
) {
    fun toDto(): cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestResultQL {
        return cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestResultQL(
            id = id,
            log = log,
            creationDate = creationDate.atOffset(ZoneOffset.UTC),
            updateDate = updateDate?.atOffset(ZoneOffset.UTC),
            status = TestStatus.getById(testStatusId!!)
        )
    }

    enum class TestStatus(val id: Long) {
        WAITING_TO_TEST(0L),
        RUNNING(1L),
        FINISHED(2L),
        ERROR(3L);

        companion object {
            val IDS_MAP = mapOf(
                0L to WAITING_TO_TEST,
                1L to RUNNING,
                2L to FINISHED,
                3L to ERROR
            )

            fun getById(id: Long) = values()[id.toInt()]
        }
    }
}