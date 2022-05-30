package cz.fei.upce.checkman.service.course

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.domain.user.GlobalRole

data class CourseAccessRequest(
    val courseId: Long,
    val semesterId: Long,
    val appUser: AppUser,
    val authorities: Set<GlobalRole>
)
