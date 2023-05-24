package cz.fei.upce.checkman.service.course.challenge.exception

class AlreadyPublishedException(challengeId: Long) : Throwable("Challenge with id $challengeId already published.") {
}