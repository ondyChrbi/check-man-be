package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.service.course.challenge.solution.TestResultService
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.upce.fei.checkman.domain.course.security.annotation.TestResultId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono

@Controller
class TestResultController(
    private val testResultService: TestResultService,
) {
    @QueryMapping
    @PreCourseSemesterAuthorize(value = [CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.VIEW_TEST_RESULT])
    fun testResult(@TestResultId @Argument id: Long, authentication: Authentication): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestResultQL> {
        return testResultService.findById(id)
    }

    @SchemaMapping(typeName = "Solution")
    fun testResult(solution: cz.fei.upce.checkman.dto.graphql.output.challenge.solution.SolutionQL?, authentication: Authentication): Mono<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestResultQL> {
        if (solution?.id == null) {
            return Mono.empty()
        }

        return testResultService.findBySolutionAsQL(solution.id!!)
    }
}