package cz.fei.upce.checkman.graphql.output.course

data class CourseDashboardQL (
    var availableCourses: List<CourseQL> = emptyList(),
    var myCourses: List<CourseQL> = emptyList()
)