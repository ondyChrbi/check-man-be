package cz.fei.upce.checkman.controller.appuser

import cz.fei.upce.checkman.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.graphql.output.course.CourseQL
import cz.fei.upce.checkman.service.appuser.AppUserService
import cz.fei.upce.checkman.service.appuser.MeService
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceImpl
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class MeQLController(
    private val appUserService: AppUserService,
    private val authenticationService: AuthenticationServiceImpl,
    private val meService: MeService
) {
    @QueryMapping
    fun me(authentication: Authentication?): Mono<AppUserQL> {
        return appUserService.meAsQL(authenticationService.extractAuthenticateUser(authentication!!))
    }

    @QueryMapping
    fun myCourses(authentication: Authentication?): Flux<CourseQL> {
        return meService.myCoursesAsQL(authenticationService.extractAuthenticateUser(authentication!!))
    }

    @QueryMapping
    fun availableCourses(authentication: Authentication?): Flux<CourseQL> {
        return meService.availableCoursesAsQL(authenticationService.extractAuthenticateUser(authentication!!))
    }

    @QueryMapping
    fun courseRoles(@Argument id: Long, authentication: Authentication?): Flux<String> {
        return meService.courseRolesAsQL(id, authenticationService.extractAuthenticateUser(authentication!!))
    }
}