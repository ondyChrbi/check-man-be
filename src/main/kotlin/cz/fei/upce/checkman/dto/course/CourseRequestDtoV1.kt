package cz.fei.upce.checkman.dto.course

import cz.fei.upce.checkman.dto.RequestDto
import java.time.LocalDateTime
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CourseRequestDtoV1(
    @field:NotEmpty(message = "{course.stag-id.not-empty}")
    var stagId: String? = null,
    @field:NotEmpty(message = "{course.name.not-empty}")
    var name: String? = null,
    @field:NotNull(message = "{course.date-creation.not-null}")
    var dateCreation: LocalDateTime? = LocalDateTime.now(),
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
