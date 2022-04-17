package cz.fei.upce.checkman.service

import cz.fei.upce.checkman.component.security.JWTUtil
import cz.fei.upce.checkman.dto.security.AuthenticationRequestDtoV1
import cz.fei.upce.checkman.dto.security.AuthenticationResponseDtoV1
import org.springframework.stereotype.Service

@Service
class AuthenticationServiceV1(private val userService : UserServiceV1, private val jwtUtil: JWTUtil) {
    fun authenticate(authenticationRequest: AuthenticationRequestDtoV1) =
        userService.findUser(authenticationRequest)
            .map { AuthenticationResponseDtoV1(jwtUtil.generateToken(authenticationRequest.stagId)) }
            .log()
}
