package cz.fei.upce.checkman.service.course.challenge.exception

class UserNotAuthorException(challengeId: Long) : Throwable("User is not author of challenge with id $challengeId")