package cz.fei.upce.checkman.conf.graphql

import graphql.scalars.ExtendedScalars
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration
class ScalarConfig {

    @Bean
    fun configurer() : RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer {
                c -> c.scalar(ExtendedScalars.DateTime)
        }
    }
}