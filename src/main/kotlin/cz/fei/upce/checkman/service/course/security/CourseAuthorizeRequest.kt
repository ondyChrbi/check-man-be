package cz.fei.upce.checkman.service.course.security

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.domain.user.GlobalRole

data class CourseAuthorizeRequest(
    val courseId: Long,
    val semesterId: Long,
    val appUser: AppUser,
    val authorities: Set<GlobalRole>
)
