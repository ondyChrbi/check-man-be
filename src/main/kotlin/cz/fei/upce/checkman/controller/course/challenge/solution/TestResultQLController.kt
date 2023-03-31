package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.graphql.output.challenge.solution.SolutionQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.TestResultQL
import cz.fei.upce.checkman.service.course.challenge.solution.TestResultServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.upce.fei.checkman.domain.course.security.annotation.TestResultId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono

@Controller
class TestResultQLController(
    private val testResultService: TestResultServiceV1,
) {
    @QueryMapping
    @PreCourseSemesterAuthorize(value = [CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.VIEW_TEST_RESULT])
    fun testResult(@TestResultId @Argument id: Long, authentication: Authentication): Mono<TestResultQL> {
        return testResultService.findById(id)
    }

    @SchemaMapping(typeName = "Solution")
    fun testResult(solution: SolutionQL?, authentication: Authentication): Mono<TestResultQL> {
        if (solution?.id == null) {
            return Mono.empty()
        }

        return testResultService.findBySolutionAsQL(solution.id!!)
    }
}