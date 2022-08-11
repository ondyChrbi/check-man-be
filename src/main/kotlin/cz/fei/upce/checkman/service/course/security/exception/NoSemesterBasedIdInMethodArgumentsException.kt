package cz.fei.upce.checkman.service.course.security.exception

import org.aspectj.lang.reflect.MethodSignature
import kotlin.reflect.KClass

class NoSemesterBasedIdInMethodArgumentsException(
    methodSignature: MethodSignature,
    requiredAnnotations: Array<KClass<out Annotation>>
) : Throwable(
    """
       One of these annotation: ${requiredAnnotations.map { it.simpleName }.joinToString { "," }} have to be included 
       in ${methodSignature.method.name} method arguments to determinate course and semester accessing to. 
    """
)
