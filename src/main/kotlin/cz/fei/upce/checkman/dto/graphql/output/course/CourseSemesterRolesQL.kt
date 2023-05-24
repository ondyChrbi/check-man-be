package cz.fei.upce.checkman.dto.graphql.output.course

data class CourseSemesterRolesQL (
    val semester: CourseSemesterQL,
    val roles: MutableList<CourseSemesterRoleQL> = mutableListOf()
)
