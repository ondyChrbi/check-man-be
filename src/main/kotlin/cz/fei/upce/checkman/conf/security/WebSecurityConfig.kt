package cz.fei.upce.checkman.conf.security

import cz.fei.upce.checkman.component.security.AuthenticationManager
import cz.fei.upce.checkman.component.security.SecurityContextRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class WebSecurityConfig(
    private val authenticationManager: AuthenticationManager,
    private val securityContextRepository: SecurityContextRepository) {

    @Value("\${spring.security.permit_paths}")
    private var permitPaths : Array<String> = arrayOf()

    @Value("\${server.ssl.enabled}")
    private var sslEnabled: Boolean = true

    @Value("\${spring.security.cors.enabled}")
    private var corsEnabled: Boolean = true

    @Value("\${check-man.security.origins}")
    private var allowedOrigins : Array<String> = arrayOf()

    @Value("\${check-man.security.headers}")
    private var allowedHeaders : String = ""

    @Value("\${check-man.security.methods}")
    private var allowedMethods : String = ""

    @Bean
    fun securityWebFilterChain(http : ServerHttpSecurity): SecurityWebFilterChain {
        var conf = http.exceptionHandling()
            .authenticationEntryPoint { swe, _ -> Mono.fromRunnable { swe.response.statusCode = HttpStatus.UNAUTHORIZED } }
            .accessDeniedHandler { swe, _ -> Mono.fromRunnable { swe.response.statusCode = HttpStatus.FORBIDDEN } }
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository)
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .pathMatchers(*permitPaths).permitAll()
            .anyExchange().authenticated().and()

        if(!corsEnabled) {
            conf = conf.cors().disable()
        }

        if (sslEnabled) { conf = conf.redirectToHttps().and() }

        return conf.build()
    }

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val corsConfig = CorsConfiguration()
        corsConfig.setAllowedOrigins(allowedOrigins.asList())
        corsConfig.setMaxAge(8000L)
        corsConfig.addAllowedMethod(allowedMethods)
        corsConfig.addAllowedHeader(allowedHeaders)

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig)

        return CorsWebFilter(source)
    }
}