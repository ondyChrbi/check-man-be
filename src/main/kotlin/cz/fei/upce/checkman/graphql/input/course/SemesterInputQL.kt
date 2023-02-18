package cz.fei.upce.checkman.graphql.input.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.graphql.InputQL
import org.springframework.validation.annotation.Validated
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Validated
data class SemesterInputQL (
    @NotNull
    @NotEmpty
    @NotBlank
    @Size(max = 1024)
    val note: String?,
    @NotNull
    val dateStart: String,
    val dateEnd: String?
): InputQL<CourseSemester> {
    override fun toEntity() = CourseSemester(
        note = note,
        dateStart = LocalDateTime.parse(dateStart),
        dateEnd = if (dateEnd != null) LocalDateTime.parse(dateEnd) else null
    )

    fun toEntity(courseId: Long): CourseSemester {
        val courseSemester = this.toEntity()
        courseSemester.courseId = courseId

        return courseSemester
    }
}