package cz.fei.upce.checkman.service.course.security

import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.CourseId
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseAuthorize
import cz.fei.upce.checkman.service.course.security.annotation.SemesterId
import cz.fei.upce.checkman.service.course.security.exception.NotCourseIdDataTypeException
import cz.fei.upce.checkman.service.course.security.exception.NotOneCourseIdInMethodException
import cz.fei.upce.checkman.service.course.security.exception.NotOneSemesterIdInMethodException
import cz.fei.upce.checkman.service.course.security.exception.NotSemesterIdDataTypeException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono

@Aspect
@Configuration
class CourseAccessProfilingAspect(
    private val authorizeService: CourseAuthorizationService,
    private val authenticationService: AuthenticationServiceV1
    ) {
    @Around("@annotation(cz.fei.upce.checkman.service.course.security.annotation.PreCourseAuthorize)")
    fun checkCourseAccess(joinPoint: ProceedingJoinPoint): Mono<out Any> {
        val annotation = (joinPoint.signature as MethodSignature).method.getAnnotation(PreCourseAuthorize::class.java)
        val parameters = (joinPoint.signature as MethodSignature).method.parameters

        val courses = parameters.filter { it.annotations.filterIsInstance<CourseId>().isNotEmpty() }
        val semesters = parameters.filter { it.annotations.filterIsInstance<SemesterId>().isNotEmpty() }

        if (courses.size != 1) { return Mono.error<Void>(NotOneCourseIdInMethodException(courses)) }
        if (semesters.size != 1) { return Mono.error<Void>(NotOneSemesterIdInMethodException(semesters)) }

        val courseId = joinPoint.args[parameters.indexOf(courses.first())]
        val semesterId = joinPoint.args[parameters.indexOf(courses.first())]

        if(courseId !is Long) { return Mono.error<Void>(NotCourseIdDataTypeException(Long::class.java)) }
        if(semesterId !is Long) { return Mono.error<Void>(NotSemesterIdDataTypeException(Long::class.java)) }

        val authentication = joinPoint.args.first { it is Authentication } as Authentication

        val appUser = authenticationService.extractAuthenticateUser(authentication)
        val authorities = authenticationService.extractAuthorities(authentication)

        val courseAccess = CourseAuthorizeRequest(courseId, semesterId, appUser, authorities)

        return authorizeService.checkCourseAccess(courseAccess, annotation.value)
            .flatMap { joinPoint.proceed() as Mono<out Any> }
    }
}