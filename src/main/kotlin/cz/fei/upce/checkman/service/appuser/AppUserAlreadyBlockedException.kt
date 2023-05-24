package cz.fei.upce.checkman.service.appuser

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.CONFLICT)
class AppUserAlreadyBlockedException : Throwable()