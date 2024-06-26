package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.dto.graphql.output.challenge.PermittedAppUserChallengeQL
import cz.fei.upce.checkman.service.course.challenge.PermitChallengeService
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.upce.fei.checkman.domain.course.security.annotation.ChallengeId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@Controller
class PermitChallengeController(private val permitChallengeService: PermitChallengeService) {
    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS])
    fun permitUserChallenge(
        @Argument @ChallengeId challengeId: Long, @Argument appUserId: Long, @Argument accessTo: OffsetDateTime = OffsetDateTime.now(),
        authentication: Authentication
    ): Mono<PermittedAppUserChallengeQL> {
        return permitChallengeService.permitToAccessAsQL(challengeId, appUserId, accessTo.toLocalDateTime())
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS])
    fun removePermitUserChallenge(
        @Argument @ChallengeId challengeId: Long, @Argument appUserId: Long,
        authentication: Authentication
    ): Mono<Boolean> {
        return permitChallengeService.removeAccessFrom(challengeId, appUserId)
    }

    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS])
    fun appUsersToPermitChallenge(
        @Argument @ChallengeId challengeId: Long,
        @Argument pageSize: Int? = CheckManApplication.DEFAULT_PAGE_SIZE,
        @Argument page: Int? = CheckManApplication.DEFAULT_PAGE,
        authentication: Authentication
    ): Flux<AppUserQL> {
        return permitChallengeService.findAllToPermitAsQL(challengeId, pageSize = pageSize, page = page)
    }

    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS])
    fun permittedAppUsersChallenge(
        @Argument @ChallengeId challengeId: Long,
        @Argument pageSize: Int? = CheckManApplication.DEFAULT_PAGE_SIZE,
        @Argument page: Int? = CheckManApplication.DEFAULT_PAGE,
        authentication: Authentication
    ): Flux<AppUserQL> {
        return permitChallengeService.finaAllPermittedAsQL(challengeId, pageSize = pageSize, page = page)
    }

    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS])
    fun searchAppUsersToPermitChallenge(
        @Argument @ChallengeId challengeId: Long,
        @Argument search: String? = "",
        @Argument pageSize: Int? = CheckManApplication.DEFAULT_PAGE_SIZE,
        @Argument page: Int? = CheckManApplication.DEFAULT_PAGE,
        authentication: Authentication
    ): Flux<AppUserQL> {
        return permitChallengeService.findAllToPermitAsQL(challengeId, search ?: "", pageSize, page)
    }

    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS])
    fun searchPermittedAppUsersChallenge(
        @Argument @ChallengeId challengeId: Long,
        @Argument search: String? = "",
        authentication: Authentication
    ): Flux<AppUserQL> {
        return permitChallengeService.finaAllPermittedAsQL(challengeId, search ?: "")
    }
}