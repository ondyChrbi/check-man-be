package cz.fei.upce.checkman.service.course.security.exception

import org.aspectj.lang.reflect.MethodSignature
import org.springframework.security.core.Authentication

class MissingAuthenticationArgumentException(methodSignature: MethodSignature) : Throwable(
    """
        Missing ${Authentication::class.java.name} in method ${methodSignature.method.name} arguments
    """
)