package cz.fei.upce.checkman.conf.redis

import cz.fei.upce.checkman.domain.user.AuthenticationExchange
import cz.fei.upce.checkman.domain.user.AuthenticationRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfiguration {
    @Bean
    fun authOperations(factory: ReactiveRedisConnectionFactory): ReactiveRedisOperations<String, AuthenticationRequest> {
        val serializer = Jackson2JsonRedisSerializer(AuthenticationRequest::class.java)
        val builder = RedisSerializationContext
            .newSerializationContext<String, AuthenticationRequest>(StringRedisSerializer())
        val context = builder.value(serializer).build()

        return ReactiveRedisTemplate(factory, context)
    }

    @Bean
    fun exchangeOperations(factory: ReactiveRedisConnectionFactory): ReactiveRedisOperations<String, AuthenticationExchange> {
        val serializer = Jackson2JsonRedisSerializer(AuthenticationExchange::class.java)
        val builder = RedisSerializationContext
            .newSerializationContext<String, AuthenticationExchange>(StringRedisSerializer())
        val context = builder.value(serializer).build()

        return ReactiveRedisTemplate(factory, context)
    }
}