package cz.fei.upce.checkman.service.role

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.CONFLICT)
class RoleAlreadyAssignedException(appUserId: Long, globalRoleId: Long) :
    Throwable("Role $globalRoleId is already assigned to user $appUserId")
