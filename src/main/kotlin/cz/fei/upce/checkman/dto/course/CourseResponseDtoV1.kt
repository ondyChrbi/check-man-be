package cz.fei.upce.checkman.dto.course

import cz.fei.upce.checkman.domain.course.Course
import cz.fei.upce.checkman.dto.ResponseDto
import java.time.LocalDateTime

data class CourseResponseDtoV1(
    var id: Long? = null,
    var stagId: String? = null,
    var name: String? = null,
    var dateCreation: LocalDateTime? = null,
    var icon: String? = null,
    var template: String? = null,
    var semesters: Collection<CourseSemesterResponseDtoV1> = emptyList()
) : ResponseDto<Course, CourseResponseDtoV1>() {
    override fun withId(id: Long?): CourseResponseDtoV1 {
        this.id = id
        return this
    }

    override fun toEntity() = Course(stagId = stagId, name = name, icon = icon, template = template)

    override fun toEntity(entity: Course): Course {
        entity.stagId = stagId
        entity.name = name
        entity.dateCreation = dateCreation
        entity.icon = icon
        entity.template = template

        return entity
    }

    fun withSemesters(semesters: Collection<CourseSemesterResponseDtoV1>): CourseResponseDtoV1 {
        this.semesters = semesters
        return this
    }

    companion object {
        fun fromEntity(course: Course) = CourseResponseDtoV1(
            course.id,
            course.stagId,
            course.name,
            course.dateCreation,
            course.icon,
            course.template
        )
    }
}
