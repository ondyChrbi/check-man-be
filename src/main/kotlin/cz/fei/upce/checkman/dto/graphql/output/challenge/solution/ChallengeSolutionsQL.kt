package cz.fei.upce.checkman.dto.graphql.output.challenge.solution

import cz.fei.upce.checkman.dto.graphql.output.challenge.ChallengeQL

data class ChallengeSolutionsQL(
    var challenge: ChallengeQL? = null,
    var solutions: List<SolutionQL> = listOf()
)
