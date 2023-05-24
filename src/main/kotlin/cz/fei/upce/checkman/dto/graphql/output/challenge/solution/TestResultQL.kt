package cz.fei.upce.checkman.dto.graphql.output.challenge.solution

import cz.fei.upce.checkman.domain.challenge.solution.TestResult
import java.time.OffsetDateTime

data class TestResultQL(
    val id: Long?,
    val log: String,
    val creationDate: OffsetDateTime,
    val updateDate: OffsetDateTime?,
    val status: TestResult.TestStatus? = null
)
