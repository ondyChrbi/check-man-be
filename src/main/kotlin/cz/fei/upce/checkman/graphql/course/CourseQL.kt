package cz.fei.upce.checkman.graphql.course

import java.time.LocalDateTime

data class CourseQL(
    val id: Long? = null,
    val stagId: String = "",
    val name: String = "",
    val dateCreation: LocalDateTime = LocalDateTime.now(),
    val icon: String? = null,
    val template: String? = null,
    val semesters: List<CourseSemesterQL> = emptyList()
)
