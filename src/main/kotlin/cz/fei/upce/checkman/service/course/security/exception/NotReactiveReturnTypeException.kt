package cz.fei.upce.checkman.service.course.security.exception

import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import org.aspectj.lang.reflect.MethodSignature
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class NotReactiveReturnTypeException(signature: MethodSignature) : Throwable(
    """
        Method ${signature.method.name} annotated with ${PreCourseSemesterAuthorize::class.simpleName} doesnt not 
        return reactive type of ${Mono::class.java.simpleName} or ${Flux::class.java.simpleName}
    """
)
