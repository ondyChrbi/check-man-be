package cz.fei.upce.checkman.dto.graphql.output.challenge.solution

import cz.fei.upce.checkman.dto.graphql.output.challenge.ChallengeQL

data class ChallengeSolutionsQL(
    var challenge: cz.fei.upce.checkman.dto.graphql.output.challenge.ChallengeQL? = null,
    var solutions: List<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.SolutionQL> = listOf()
)
