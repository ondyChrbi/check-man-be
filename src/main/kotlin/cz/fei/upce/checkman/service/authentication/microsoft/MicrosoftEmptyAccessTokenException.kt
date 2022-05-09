package cz.fei.upce.checkman.service.authentication.microsoft

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
class MicrosoftEmptyAccessTokenException : Throwable("Microsoft API provide empty access token.")
