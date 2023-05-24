package cz.fei.upce.checkman.service.course.challenge.exception

import cz.fei.upce.checkman.domain.challenge.Challenge
import cz.fei.upce.checkman.domain.user.AppUser

class UserNotAuthorizedToViewChallengeException(challenge: Challenge, appUser: AppUser) : Exception("""
    User ${appUser.id} is not authorized to view challenge ${challenge.name}
""".trimIndent())