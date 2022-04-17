package cz.fei.upce.checkman.service

import cz.fei.upce.checkman.component.security.JWTUtil
import org.springframework.stereotype.Service

@Service
class AuthenticationServiceV1(private val userService : UserServiceV1, private val jwtUtil: JWTUtil)