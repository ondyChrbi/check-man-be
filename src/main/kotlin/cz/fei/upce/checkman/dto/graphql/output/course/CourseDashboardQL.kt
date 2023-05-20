package cz.fei.upce.checkman.dto.graphql.output.course

data class CourseDashboardQL (
    var availableCourses: List<CourseQL> = emptyList(),
    var myCourses: List<CourseQL> = emptyList()
)