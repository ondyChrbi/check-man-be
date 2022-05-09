package cz.fei.upce.checkman.dto.course

import cz.fei.upce.checkman.domain.course.Course
import cz.fei.upce.checkman.dto.BaseDto
import java.time.LocalDateTime

data class CourseDtoV1(
    var id: Long? = null,
    var stagId: String? = null,
    var name: String? = null,
    var dateCreation: LocalDateTime? = null,
    var icon: String? = null,
    var template: String? = null,
    var semesters: Collection<CourseSemesterDtoV1> = emptyList()
) : BaseDto<Course, CourseDtoV1> {
    override fun withId(id: Long?): CourseDtoV1 {
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

    fun withSemesters(semesters: Collection<CourseSemesterDtoV1>): CourseDtoV1 {
        this.semesters = semesters
        return this
    }
}
