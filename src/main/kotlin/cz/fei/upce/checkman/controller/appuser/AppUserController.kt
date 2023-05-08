package cz.fei.upce.checkman.controller.appuser

import cz.fei.upce.checkman.CheckManApplication
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
                      @Argument size: Int? = CheckManApplication.DEFAULT_SIZE
    ): Flux<cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL> {
        return appUserService.findAllRelatedToCourseByQL(semestersQL)
            .map { it.toQL() }
    }

    @SchemaMapping(typeName = "AppUser")
    fun roles (appUserQL: cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL, @SemesterId @Argument semesterId: Long = -1L): Flux<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterRoleQL> {
        return courseSemesterRoleService.findAllRolesByUserAndCourseSemesterAsQL(appUserQL, semesterId)
    }

    @SchemaMapping(typeName = "Challenge")
    fun author(challenge: cz.fei.upce.checkman.dto.graphql.output.challenge.ChallengeQL): Mono<cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL> {
        return appUserService.findAuthor(challenge.id!!)
            .map { it.toQL() }
    }

    @QueryMapping
    fun appUser(@Argument id: Long): Mono<cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL> {
        return appUserService.findByIdAsQL(id)
    }

    @SchemaMapping(typeName = "PermittedAppUserChallenge", field = "appUser")
    fun appUserPermittedAppUserChallenge(permittedAppUserChallenge: cz.fei.upce.checkman.dto.graphql.output.challenge.PermittedAppUserChallengeQL): Mono<cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL> {
        return appUserService.findByPermittedAppUserChallengeIdAsQL(permittedAppUserChallenge.id!!)
    }
}