package cz.fei.upce.checkman.service.course.security.exception

import cz.fei.upce.checkman.service.course.security.annotation.ChallengeId
import kotlin.reflect.KClass

class AuthorizeIdentificationNotFoundException(annotation: KClass<ChallengeId>, id: Long) : Throwable(
    """
        ${annotation::class.java.simpleName} with value ${id} not found. Authorization failed.
    """
)
