package cz.fei.upce.checkman.graphql.output.challenge.solution

import cz.fei.upce.checkman.graphql.output.course.CourseSemesterQL

data class CoursesReviewListQL(
    var course : CourseSemesterQL? = null,
    var reviews: ChallengeSolutionsQL? = null,
)
