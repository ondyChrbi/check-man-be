package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.service.course.challenge.ChallengeServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.fei.upce.checkman.service.course.security.annotation.SemesterId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller

@Controller
class ChallengeQLController(private val challengeService : ChallengeServiceV1) {
    @QueryMapping
    @PreCourseSemesterAuthorize
    fun challenges(@SemesterId @Argument semesterId: Long, authentication: Authentication) =
        challengeService.findAllBySemesterIdAsQL(semesterId)
}