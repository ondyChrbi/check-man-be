package cz.fei.upce.checkman.dto.graphql.input.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.dto.graphql.InputQL
import org.springframework.validation.annotation.Validated
import java.time.OffsetDateTime
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
    val dateStart: OffsetDateTime,
    val dateEnd: OffsetDateTime?
): cz.fei.upce.checkman.dto.graphql.InputQL<CourseSemester> {
    override fun toEntity() = CourseSemester(
        note = note,
        dateStart = dateStart.toLocalDateTime(),
        dateEnd = dateEnd?.toLocalDateTime()
    )

    fun toEntity(courseId: Long): CourseSemester {
        val courseSemester = this.toEntity()
        courseSemester.courseId = courseId

        return courseSemester
    }
}