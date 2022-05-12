package cz.fei.upce.checkman.dto.course

import cz.fei.upce.checkman.dto.RequestDto
import java.time.LocalDateTime

data class CourseRequestDtoV1(
    var stagId: String? = null,
    var name: String? = null,
    var dateCreation: LocalDateTime? = null,
    var icon: String? = null,
    var template: String? = null,
    var semesters: Collection<CourseSemesterRequestDtoV1>? = emptyList()
) : RequestDto<CourseRequestDtoV1, CourseResponseDtoV1> {
    override fun toResponseDto() = CourseResponseDtoV1(
        stagId = stagId,
        name = name,
        dateCreation = dateCreation,
        icon = icon,
        template = template,
        semesters = semesters!!.map { it.toResponseDto() }
    )

    override fun preventNullCollections(): CourseRequestDtoV1 {
        if (semesters == null) { semesters = emptyList() }
        return this
    }
}
