package cz.fei.upce.checkman.dto.graphql.output.challenge.solution

import cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterQL

data class CoursesReviewListQL(
    var course : cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterQL? = null,
    var reviews: List<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.ChallengeSolutionsQL> = listOf(),
)
