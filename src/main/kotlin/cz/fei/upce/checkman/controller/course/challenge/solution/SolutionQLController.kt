package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.service.course.challenge.solution.SolutionService
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated

@Controller
@Validated
class SolutionQLController(private val solutionService: SolutionService) {

}