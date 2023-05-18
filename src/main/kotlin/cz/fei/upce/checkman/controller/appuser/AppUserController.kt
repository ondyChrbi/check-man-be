package cz.fei.upce.checkman.controller.appuser

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.ChallengeQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.PermittedAppUserChallengeQL
import cz.fei.upce.checkman.service.appuser.AppUserService
import cz.upce.fei.checkman.domain.course.security.annotation.SemesterId
import cz.fei.upce.checkman.service.role.CourseSemesterRoleService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
@Validated
class AppUserController(
    private val appUserService: AppUserService,
    private val courseSemesterRoleService : CourseSemesterRoleService,
) {
    @SchemaMapping(typeName = "Semester")
    fun relatedUsers (semestersQL: cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterQL,
                      @Argument offset: Int? = CheckManApplication.DEFAULT_OFFSET,
                      @Argument size: Int? = CheckManApplication.DEFAULT_LIMIT
    ): Flux<AppUserQL> {
        return appUserService.findAllRelatedToCourseByQL(semestersQL)
            .map { it.toQL() }
    }

    @SchemaMapping(typeName = "AppUser")
    fun roles (appUserQL: AppUserQL, @SemesterId @Argument semesterId: Long = -1L): Flux<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterRoleQL> {
        return courseSemesterRoleService.findAllRolesByUserAndCourseSemesterAsQL(appUserQL, semesterId)
    }

    @SchemaMapping(typeName = "Challenge")
    fun author(challenge: ChallengeQL): Mono<AppUserQL> {
        return appUserService.findAuthor(challenge.id!!)
            .map { it.toQL() }
    }

    @QueryMapping
    fun appUser(@Argument id: Long): Mono<AppUserQL> {
        return appUserService.findByIdAsQL(id)
    }

    @SchemaMapping(typeName = "PermittedAppUserChallenge", field = "appUser")
    fun appUserPermittedAppUserChallenge(permittedAppUserChallenge: PermittedAppUserChallengeQL): Mono<AppUserQL> {
        return appUserService.findByPermittedAppUserChallengeIdAsQL(permittedAppUserChallenge.id!!)
    }

    @SchemaMapping(typeName = "Challenge", field = "relatedUsers")
    fun relatedUsersChallenge(
        challenge: ChallengeQL,
        @Argument pageSize: Int? = CheckManApplication.DEFAULT_LIMIT,
        @Argument page: Int? = CheckManApplication.DEFAULT_OFFSET
    ): Flux<AppUserQL> {
        return appUserService.findAllRelatedToChallenge(challenge.id!!, pageSize, page)
            .map { it.toQL() }
    }
}