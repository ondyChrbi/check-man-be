package cz.fei.upce.checkman.graphql.input.course

import cz.fei.upce.checkman.domain.course.Course
import cz.fei.upce.checkman.graphql.InputQL

data class CourseInputQL(
    val stagId: String,
    val name: String,
    val icon: String?,
    val template: String?,
    val semesters: List<SemesterInputQL> = emptyList()
) : InputQL<Course> {
    override fun toEntity() = Course(name = name, icon = icon, template = template)
}
