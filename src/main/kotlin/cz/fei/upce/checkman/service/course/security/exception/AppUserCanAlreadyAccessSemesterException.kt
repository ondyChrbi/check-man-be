package cz.fei.upce.checkman.service.course.security.exception

import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.domain.user.AppUser

class AppUserCanAlreadyAccessSemesterException(appUser: AppUser, courseSemester: CourseSemester) : Throwable(
    """
        User ${appUser.stagId} (${appUser.id}) can already access course ${courseSemester.id}
    """
)
