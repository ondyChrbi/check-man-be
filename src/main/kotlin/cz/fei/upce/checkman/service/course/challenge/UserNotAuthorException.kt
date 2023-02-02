package cz.fei.upce.checkman.service.course.challenge

class UserNotAuthorException(challengeId: Long) : Throwable("User is not author of challenge with id $challengeId")