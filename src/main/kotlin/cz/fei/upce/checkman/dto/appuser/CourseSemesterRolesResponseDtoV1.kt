package cz.fei.upce.checkman.dto.appuser

import cz.fei.upce.checkman.dto.course.CourseSemesterResponseDtoV1

data class CourseSemesterRolesResponseDtoV1 (
    var semester: CourseSemesterResponseDtoV1,
    var roles: Collection<CourseSemesterRoleDtoV1> = emptyList()
) {
    fun withSemester(semester: CourseSemesterResponseDtoV1): CourseSemesterRolesResponseDtoV1 {
        this.semester = semester

        return this
    }

    fun withRoles(roles: Collection<CourseSemesterRoleDtoV1>): CourseSemesterRolesResponseDtoV1 {
        this.roles = roles

        return this
    }
}