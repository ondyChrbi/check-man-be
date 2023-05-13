package cz.fei.upce.checkman.service.appuser

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_OFFSET
import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_SIZE
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.appuser.AppUserResponseDtoV1
import cz.fei.upce.checkman.dto.appuser.GlobalRoleResponseDtoV1
import cz.fei.upce.checkman.repository.user.AppUserRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import cz.fei.upce.checkman.service.role.CourseSemesterRoleService
import cz.fei.upce.checkman.service.role.GlobalRoleService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class AppUserService(
    private val appUserRepository: AppUserRepository,
    private val globalRoleService: GlobalRoleService,
    private val courseSemesterRoleService: CourseSemesterRoleService
) {
    fun findByStagId(stagId: String) = appUserRepository.findByStagIdEquals(stagId)

    fun findById(id: Long): Mono<AppUser> {
        return appUserRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
    }

    fun findByIdAsQL(id: Long): Mono<cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL> {
        return appUserRepository.findById(id)
            .map { it.toQL() }
    }

    fun updateLastAccessDate(appUser: AppUser): Mono<AppUser> {
        appUser.lastAccessDate = LocalDateTime.now()
        return appUserRepository.save(appUser)
    }

    fun updateLastAccessDate(stagId: String): Mono<AppUser> {
        return appUserRepository.findByStagIdEquals(stagId)
            .flatMap(this::updateLastAccessDate)
    }

    fun save(appUser: AppUser) = appUserRepository.save(appUser)
        .flatMap { appUserRepository.findById(appUser.id!!) }

    fun meAsDto(loggedUser: AppUser): Mono<AppUserResponseDtoV1> {
        val responseDto = AppUserResponseDtoV1.fromEntity(loggedUser)

        return globalRoleService.rolesByUser(loggedUser)
            .map { GlobalRoleResponseDtoV1.fromEntity(it) }
            .collectList()
            .doOnNext { responseDto.withGlobalRoles(it) }
            .flatMapMany { courseSemesterRoleService.findAllSemestersAndRolesAsDto(loggedUser) }
            .collectList()
            .map { responseDto.withCourseRoles(it) }
    }

    fun meAsQL(loggedUser: AppUser): Mono<cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL> {
        val ql = loggedUser.toQL()

        return globalRoleService.rolesByUser(loggedUser)
            .map { it.toQL() }
            .collectList()
            .doOnNext { ql.globalRoles.addAll(it) }
            .flatMapMany { courseSemesterRoleService.findAllSemestersAndRolesAsQL(loggedUser) }
            .collectList()
            .doOnNext { ql.courseRoles.addAll(it) }
            .map { ql }
    }

    fun block(stagId: String): Mono<AppUser> {
        return appUserRepository.findByStagIdEquals(stagId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { appUser ->
                if (appUser.disabled) {
                    Mono.error(AppUserAlreadyBlockedException())
                } else {
                    appUser.disabled = true
                    appUserRepository.save(appUser)
                }
            }
    }

    fun unblock(stagId: String): Mono<AppUser> {
        return appUserRepository.findByStagIdEquals(stagId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { appUser ->
                if (!appUser.disabled) {
                    Mono.error(AppUserNotBlockedException())
                } else {
                    appUser.disabled = false
                    appUserRepository.save(appUser)
                }
            }
    }

    fun findAllRelatedToCourseByQL(semesterQL : cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterQL, offset: Int = DEFAULT_OFFSET, size: Int = DEFAULT_SIZE): Flux<AppUser> {
        return appUserRepository.findAllByCourseSemester(semesterQL.id, offset, size)
    }

    fun findAuthor(challengeId: Long): Mono<AppUser> {
        return appUserRepository.findAuthorByChallengeId(challengeId)
    }

    fun findAllPermitToChallenge(challengeId: Long): Flux<AppUser> {
        return appUserRepository.findAllPermitToChallenge(challengeId)
    }

    fun searchAllPermitToChallenge(challengeId: Long, search: String = ""): Flux<AppUser> {
        return appUserRepository.searchAllPermitToChallenge(challengeId, search = search)
    }

    fun findAllPermittedToChallenge(challengeId: Long): Flux<AppUser> {
        return appUserRepository.findAllPermittedToChallenge(challengeId)
    }

    fun searchAllPermittedToChallenge(challengeId: Long, search: String = ""): Flux<AppUser> {
        return appUserRepository.searchAllPermittedToChallenge(challengeId, search)
    }

    fun findByPermittedAppUserChallengeIdAsQL(id: Long): Mono<cz.fei.upce.checkman.dto.graphql.output.appuser.AppUserQL> {
        return appUserRepository.findByPermittedChallengeId(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { it.toQL() }
    }

    fun findAllRelatedToChallenge(
        challengeId: Long,
        pageSize: Int? = DEFAULT_SIZE,
        page: Int? = DEFAULT_OFFSET
    ): Flux<AppUser> {
        return appUserRepository.findAllRelatedToChallenge(challengeId, pageSize ?: DEFAULT_SIZE, page ?: DEFAULT_OFFSET)
    }
}
