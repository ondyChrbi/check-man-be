package cz.fei.upce.checkman.service.course.challenge.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class AppUserDoesntHaveAccessToChallengeException : Throwable()