package cz.fei.upce.checkman.controller.course.challenge

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.dto.course.challenge.ChallengeSummaryQL
import cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.service.course.challenge.ChallengeSummaryService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux

@Controller
class ChallengeSummaryController(private val challengeSummaryService : ChallengeSummaryService) {
    @SchemaMapping(typeName = "AppUser")
    fun challengeSummary(appUser: AppUserQL, @Argument challengeId: Long, @Argument limit: Int?, @Argument offset: Int?): Flux<ChallengeSummaryQL> {
        return challengeSummaryService.findByAppUserAndChallenge(
            appUser.id!!,
            challengeId,
            limit ?: CheckManApplication.DEFAULT_LIMIT,
            offset ?: CheckManApplication.DEFAULT_OFFSET,
        ).map { it.toQL() }
    }

}
