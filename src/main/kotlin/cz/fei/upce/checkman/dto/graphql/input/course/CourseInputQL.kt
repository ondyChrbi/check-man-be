package cz.fei.upce.checkman.dto.graphql.input.course

import cz.fei.upce.checkman.domain.course.Course
import cz.fei.upce.checkman.dto.graphql.InputQL
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Validated
data class CourseInputQL(
    @NotNull
    @NotEmpty
    @NotBlank
    @Size(max = 128)
    val stagId: String,
    @NotNull
    @NotEmpty
    @NotBlank
    @Size(max = 256)
    val name: String,
    @NotBlank
    @Size(max = 1024)
    val icon: String?,
    @NotBlank
    @Size(max = 256)
    val template: String?,
    val semesters: List<cz.fei.upce.checkman.dto.graphql.input.course.SemesterInputQL> = emptyList()
) : cz.fei.upce.checkman.dto.graphql.InputQL<Course> {
    override fun toEntity() = Course(stagId = stagId, name = name, icon = icon, template = template)
}
