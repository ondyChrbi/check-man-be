package cz.fei.upce.checkman.dto.graphql.output.course

data class CourseDashboardQL (
    var availableCourses: List<cz.fei.upce.checkman.dto.graphql.output.course.CourseQL> = emptyList(),
    var myCourses: List<cz.fei.upce.checkman.dto.graphql.output.course.CourseQL> = emptyList()
)