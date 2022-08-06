package cz.fei.upce.checkman.service.course

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class AppUserCourseSemesterForbiddenException : Throwable("Missing permissions")
