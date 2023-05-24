package cz.fei.upce.checkman.service.authentication.microsoft

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
class MicrosoftNotValidUserCredentialsException(message: String) : Throwable("Not valid Microsoft credential: $message")