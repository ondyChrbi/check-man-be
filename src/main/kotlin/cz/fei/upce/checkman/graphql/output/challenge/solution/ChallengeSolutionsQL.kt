package cz.fei.upce.checkman.graphql.output.challenge.solution

import cz.fei.upce.checkman.graphql.output.challenge.ChallengeQL

data class ChallengeSolutionsQL(
    var challenge: ChallengeQL? = null,
    var solutions: List<SolutionQL> = listOf()
)
