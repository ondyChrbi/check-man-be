package cz.fei.upce.checkman.graphql.output.course

data class CourseRequirementsQL (
    val minOptional: Int = 0,
    val minMandatory: Int = 0,
    val minCredit: Int = 0,
    val minExam: Int = 0
)
