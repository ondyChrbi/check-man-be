package cz.fei.upce.checkman.controller.appuser

import cz.fei.upce.checkman.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.graphql.output.course.CourseQL
import cz.fei.upce.checkman.service.appuser.AppUserServiceV1
import cz.fei.upce.checkman.service.appuser.MeServiceV1
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class MeQLController(
    private val appUserService: AppUserServiceV1,
    private val authenticationService: AuthenticationServiceV1,
    private val meServiceV1: MeServiceV1
) {
    @QueryMapping
    fun me(authentication: Authentication?): Mono<AppUserQL> {
        return appUserService.meAsQL(authenticationService.extractAuthenticateUser(authentication!!))
    }

    @QueryMapping
    fun myCourses(authentication: Authentication?): Flux<CourseQL> {
        return meServiceV1.myCoursesAsQL(authenticationService.extractAuthenticateUser(authentication!!))
    }

    @QueryMapping
    fun availableCourses(authentication: Authentication?): Flux<CourseQL> {
        return meServiceV1.availableCoursesAsQL(authenticationService.extractAuthenticateUser(authentication!!))
    }
}