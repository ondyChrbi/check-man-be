package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.graphql.input.course.RequirementInputQL
import cz.fei.upce.checkman.service.course.challenge.requirement.RequirementServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.ChallengeId
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.fei.upce.checkman.service.course.security.annotation.RequirementId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller

@Controller
class RequirementQLController(private val requirementServiceV1: RequirementServiceV1) {
    @QueryMapping(name = "requirements")
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS])
    fun requirements(@ChallengeId @Argument challengeId: Long, authentication: Authentication) =
        requirementServiceV1.findByChallengeIdAsQL(challengeId)

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun createRequirement(@ChallengeId @Argument challengeId: Long, @Argument input: RequirementInputQL, authentication: Authentication) =
        requirementServiceV1.addAsQL(challengeId, input)

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun editRequirement(@RequirementId @Argument requirementId: Long, @Argument input: RequirementInputQL, authentication: Authentication) =
        requirementServiceV1.editAsQL(requirementId, input)

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_CHALLENGE])
    fun removeRequirement(@RequirementId @Argument requirementId: Long, authentication: Authentication) = requirementServiceV1.removeAsQL(requirementId)
}