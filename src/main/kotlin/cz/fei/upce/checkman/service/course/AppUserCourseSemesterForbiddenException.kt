package cz.fei.upce.checkman.service.course

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class AppUserCourseSemesterForbiddenException(role: CourseSemesterRole.Value) : Throwable("Missing permission: ${role.name}")
