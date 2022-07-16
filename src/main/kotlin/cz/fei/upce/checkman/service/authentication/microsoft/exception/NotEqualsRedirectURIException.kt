package cz.fei.upce.checkman.service.authentication.microsoft.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class NotEqualsRedirectURIException : Throwable()
