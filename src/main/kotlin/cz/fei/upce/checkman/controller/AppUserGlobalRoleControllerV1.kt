package cz.fei.upce.checkman.controller

import cz.fei.upce.checkman.doc.RemoveGlobalRoleToUserEndpointV1
import cz.fei.upce.checkman.doc.role.AssignGlobalRoleToUserEndpointV1
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_GLOBAL_ROLE_MANAGE
import cz.fei.upce.checkman.dto.role.global.AppUserGlobalRoleDtoV1
import cz.fei.upce.checkman.service.role.GlobalRoleService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/v1/global-role")
@Tag(name = "Global roles V1", description = "Global roles API (V1)")
class AppUserGlobalRoleControllerV1(private val globalRoleService: GlobalRoleService) {
    @PutMapping("/app-user/assign")
    @PreAuthorize("hasRole('$ROLE_GLOBAL_ROLE_MANAGE')")
    @AssignGlobalRoleToUserEndpointV1
    fun assign(@Valid @RequestBody appUserGlobalRole: AppUserGlobalRoleDtoV1) =
        globalRoleService.assign(appUserGlobalRole).map { ResponseEntity.noContent().build<String>() }

    @PutMapping("/app-user/remove")
    @PreAuthorize("hasRole('$ROLE_GLOBAL_ROLE_MANAGE')")
    @RemoveGlobalRoleToUserEndpointV1
    fun remove(@Valid @RequestBody appUserGlobalRole: AppUserGlobalRoleDtoV1) =
        globalRoleService.remove(appUserGlobalRole).map { ResponseEntity.noContent().build<String>() }
}
