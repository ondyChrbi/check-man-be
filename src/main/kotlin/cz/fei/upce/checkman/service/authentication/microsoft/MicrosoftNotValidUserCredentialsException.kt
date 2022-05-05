package cz.fei.upce.checkman.service.authentication.microsoft

class MicrosoftNotValidUserCredentialsException(message: String) : Throwable("Not valid Microsoft credential: $message")