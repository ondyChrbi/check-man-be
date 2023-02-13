package cz.fei.upce.checkman.controller.course.challenge.solution

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.graphql.input.course.challenge.solution.ReviewPointsInputQL
import cz.fei.upce.checkman.service.course.challenge.requirement.RequirementServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.fei.upce.checkman.service.course.security.annotation.RequirementId
import cz.fei.upce.checkman.service.course.security.annotation.ReviewId
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import reactor.core.publisher.Mono

@Controller
@Validated
class RequirementReviewQLController(
    private val requirementService: RequirementServiceV1
) {
    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.REVIEW_CHALLENGE])
    fun addReviewPoints(@ReviewId @Argument reviewId: Long, @RequirementId requirementId: Long, @Argument reviewPoints : ReviewPointsInputQL, authentication: Authentication): Mono<Boolean> {
        return requirementService.add(reviewId, requirementId, reviewPoints)
    }
}