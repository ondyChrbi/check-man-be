package cz.fei.upce.checkman.dto.graphql.input.course

import cz.fei.upce.checkman.domain.course.CourseRequirements

data class CourseRequirementsInputQL (
    val minOptional: Int = 0,
    val minMandatory: Int = 0,
    val minCredit: Int = 0,
    val minExam: Int = 0
) {
    fun toEntity(semesterId: Long): CourseRequirements {
        return CourseRequirements(
            minOptional = minOptional,
            minMandatory = minMandatory,
            minCredit = minCredit,
            minExam = minExam,
            courseSemesterId = semesterId
        )
    }
}
