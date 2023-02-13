package cz.fei.upce.checkman.controller.appuser

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.graphql.output.challenge.ChallengeQL
import cz.fei.upce.checkman.graphql.output.course.CourseSemesterQL
import cz.fei.upce.checkman.graphql.output.course.CourseSemesterRoleQL
import cz.fei.upce.checkman.service.appuser.AppUserServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.SemesterId
import cz.fei.upce.checkman.service.role.CourseSemesterRoleServiceV1
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
@Validated
class AppUserQLController(
    private val appUserService: AppUserServiceV1,
    private val courseSemesterRoleService : CourseSemesterRoleServiceV1,
) {
    @SchemaMapping(typeName = "Semester")
    fun relatedUsers (semestersQL: CourseSemesterQL,
               @Argument offset: Int? = CheckManApplication.DEFAULT_OFFSET,
               @Argument size: Int? = CheckManApplication.DEFAULT_SIZE
    ): Flux<AppUserQL> {
        return appUserService.findAllRelatedToCourseByQL(semestersQL)
            .map { it.toQL() }
    }

    @SchemaMapping(typeName = "AppUser")
    fun roles (appUserQL: AppUserQL, @SemesterId @Argument semesterId: Long = -1L): Flux<CourseSemesterRoleQL> {
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
}