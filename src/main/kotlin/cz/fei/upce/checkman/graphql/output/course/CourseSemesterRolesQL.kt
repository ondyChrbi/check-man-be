package cz.fei.upce.checkman.graphql.output.course

data class CourseSemesterRolesQL (
    val semester: CourseSemesterQL,
    val roles: MutableList<CourseSemesterRoleQL> = mutableListOf()
)
