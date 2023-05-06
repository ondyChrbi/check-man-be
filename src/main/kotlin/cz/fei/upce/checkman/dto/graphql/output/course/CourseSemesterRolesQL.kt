package cz.fei.upce.checkman.dto.graphql.output.course

data class CourseSemesterRolesQL (
    val semester: cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterQL,
    val roles: MutableList<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterRoleQL> = mutableListOf()
)
