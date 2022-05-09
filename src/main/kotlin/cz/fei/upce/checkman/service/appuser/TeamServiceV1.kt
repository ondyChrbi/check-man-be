package cz.fei.upce.checkman.service.appuser

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.domain.user.AppUserTeam
import cz.fei.upce.checkman.domain.user.Team
import cz.fei.upce.checkman.repository.user.AppUserTeamRepository
import cz.fei.upce.checkman.repository.user.TeamRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TeamServiceV1(private val teamRepository: TeamRepository, private val userTeamRepository: AppUserTeamRepository) {
    @Value("check-man.team.private.post-fix")
    private var privateTeamPostFix = "private"

    fun createPersonalTeam(appUser: AppUser) = createTeam(appUser)
        .flatMap { createConnection(appUser, it) }
        .flatMap { teamRepository.findById(it.teamId!!) }

    fun createTeam(appUser: AppUser) = teamRepository.save(
        Team(
            name = createPrivateTeamName(appUser.stagId!!, privateTeamPostFix),
            creationDate = LocalDateTime.now(),
            minMembers = PRIVATE_TEAM_MIN_MEMBERS,
            maxMembers = PRIVATE_TEAM_MAX_MEMBERS,
            private = true
        )
    )

    private fun createConnection(appUser: AppUser, team: Team) =
        userTeamRepository.save(AppUserTeam(teamId = team.id, appUserId = appUser.id))

    companion object {
        const val PRIVATE_TEAM_MIN_MEMBERS = 1
        const val PRIVATE_TEAM_MAX_MEMBERS = 1

        fun createPrivateTeamName(stagId : String, postFix : String) = "${stagId}_$postFix"
    }
}