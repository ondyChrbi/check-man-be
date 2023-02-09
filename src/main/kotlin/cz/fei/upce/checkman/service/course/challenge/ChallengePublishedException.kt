package cz.fei.upce.checkman.service.course.challenge

class ChallengePublishedException(message: String) : Throwable(message) {
    constructor(challengeId: Long) : this("Challenge with ${challengeId} is already published.")
}