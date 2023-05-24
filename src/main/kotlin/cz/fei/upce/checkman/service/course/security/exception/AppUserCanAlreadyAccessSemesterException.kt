package cz.fei.upce.checkman.service.course.security.exception

import cz.fei.upce.checkman.domain.course.Semester
import cz.fei.upce.checkman.domain.user.AppUser

class AppUserCanAlreadyAccessSemesterException(appUser: AppUser, semesterId: Long) : Throwable(
    """
        User ${appUser.stagId} (${appUser.id}) can already access course $semesterId
    """
) {
    constructor(appUser: AppUser, semester: Semester) : this(appUser, semester.id!!)
}
