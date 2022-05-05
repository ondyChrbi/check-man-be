package cz.fei.upce.checkman.service.authentication.microsoft

class NotUniversityMicrosoftAccountException(domain: String) : Throwable("Not Microsoft account from current university: $domain")