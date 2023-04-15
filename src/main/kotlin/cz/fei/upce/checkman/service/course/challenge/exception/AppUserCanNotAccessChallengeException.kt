package cz.fei.upce.checkman.service.course.challenge.exception

class AppUserCanNotAccessChallengeException(challengeId: Long, appUserId: Long) : Exception("""
    User $appUserId can not access challenge $challengeId
""")
