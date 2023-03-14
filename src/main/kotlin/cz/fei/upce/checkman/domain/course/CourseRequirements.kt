package cz.fei.upce.checkman.domain.course

import cz.fei.upce.checkman.graphql.output.course.CourseRequirementsQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("course_requirements")
data class CourseRequirements(
    @Id var id: Long? = null,
    var minOptional: Int = 0,
    var minMandatory: Int = 0,
    var minCredit: Int = 0,
    var minExam: Int = 0,
    var courseSemesterId: Long = -1L,
) {
    fun toDto(): CourseRequirementsQL {
        return CourseRequirementsQL(
            minOptional = minOptional,
            minMandatory = minMandatory,
            minCredit = minCredit,
            minExam = minExam
        )
    }
}
