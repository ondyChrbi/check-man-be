package cz.fei.upce.checkman.graphql.input.course.challenge

import org.springframework.validation.annotation.Validated

@Validated
data class ReviewInputQL(
    var description: String = ""
)
