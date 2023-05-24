package cz.fei.upce.checkman.service.authentication.microsoft

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
class NotUniversityMicrosoftAccountException(domain: String) : Throwable("Not Microsoft account from current university: $domain")