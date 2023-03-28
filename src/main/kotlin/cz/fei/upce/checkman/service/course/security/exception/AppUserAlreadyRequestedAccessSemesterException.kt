package cz.fei.upce.checkman.service.course.security.exception

import cz.fei.upce.checkman.domain.user.AppUser

class AppUserAlreadyRequestedAccessSemesterException(appUser: AppUser, semesterId: Long) : Throwable(
    """
        User ${appUser.stagId} (${appUser.id}) already send access request to course $semesterId
    """
)