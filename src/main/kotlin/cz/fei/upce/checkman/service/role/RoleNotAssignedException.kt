package cz.fei.upce.checkman.service.role

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.CONFLICT)
class RoleNotAssignedException(appUserId: Long, globalRoleId: Long) :
    Throwable("Role $globalRoleId is not assigned to user $appUserId")