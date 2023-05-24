package cz.fei.upce.checkman.service.course.security.exception

import kotlin.reflect.KClass

class AuthorizeIdentificationNotFoundException(annotation: KClass<*>, id: Long) : Throwable(
    """
        ${annotation::class.java.simpleName} with value $id not found. Authorization failed.
    """
)
