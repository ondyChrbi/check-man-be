package cz.fei.upce.checkman.controller.appuser

import cz.fei.upce.checkman.doc.appuser.BlockAppUserEndpointV1
import cz.fei.upce.checkman.doc.appuser.UnblockAppUserEndpointV1
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_BLOCK_APP_USER
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_MANAGE_APP_USER
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_UNBLOCK_APP_USER
import cz.fei.upce.checkman.service.appuser.AppUserServiceV1
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/v1/app-user")
class AppUserControllerV1(private val appUserService: AppUserServiceV1) {
    @PostMapping("/{stagId}/block")
    @PreAuthorize("hasAnyRole('$ROLE_MANAGE_APP_USER', '$ROLE_BLOCK_APP_USER')")
    @BlockAppUserEndpointV1
    fun disable(@PathVariable stagId: String): Mono<ResponseEntity<String>> {
        return appUserService.block(stagId).map { ResponseEntity.noContent().build() }
    }

    @PostMapping("/{stagId}/unblock")
    @PreAuthorize("hasAnyRole('$ROLE_MANAGE_APP_USER', '$ROLE_UNBLOCK_APP_USER')")
    @UnblockAppUserEndpointV1
    fun enable(@PathVariable stagId: String): Mono<ResponseEntity<String>> {
        return appUserService.unblock(stagId).map { ResponseEntity.noContent().build() }
    }
}