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
    fun authentication(handler : AuthenticationHandlerV1) = RouterFunctions.route()
        .POST("$ROOT_PATH/login", RequestPredicates.contentType(MediaType.APPLICATION_JSON), handler::login)
        .POST("$ROOT_PATH/register", RequestPredicates.contentType(MediaType.APPLICATION_JSON), handler::register)
        .build()

    companion object{
        const val ROOT_PATH = "/v1/authentication"
    }
}
