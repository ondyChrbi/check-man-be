package cz.fei.upce.checkman.service.course.challenge.exception

class AppUserCanAlreadyAccessChallengeException(challengeId: Long, appUserId: Long) : Exception("""
    User $appUserId can already access challenge $challengeId
""")
