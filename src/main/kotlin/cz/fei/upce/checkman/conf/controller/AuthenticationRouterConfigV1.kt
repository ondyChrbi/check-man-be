package cz.fei.upce.checkman.conf.controller

import cz.fei.upce.checkman.handler.AuthenticationHandlerV1
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunctions

@Configuration
class AuthenticationRouterConfigV1 {
    @Bean
    fun authenticationRoute(handler : AuthenticationHandlerV1) = RouterFunctions.route()
        .POST("$ROOT_PATH/login", RequestPredicates.contentType(MediaType.APPLICATION_JSON), handler::login)
        .build()

    private companion object{
        const val ROOT_PATH = "/v1/authentication"
    }
}
