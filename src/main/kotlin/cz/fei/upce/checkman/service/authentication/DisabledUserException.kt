package cz.fei.upce.checkman.service.authentication

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class DisabledUserException : Throwable()