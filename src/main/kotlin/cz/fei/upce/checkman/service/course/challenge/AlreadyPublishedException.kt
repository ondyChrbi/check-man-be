package cz.fei.upce.checkman.service.course.challenge

class AlreadyPublishedException(challengeId: Long) : Throwable("Challenge with id $challengeId already published.") {
}