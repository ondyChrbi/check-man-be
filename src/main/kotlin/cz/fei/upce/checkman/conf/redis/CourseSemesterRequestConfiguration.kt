package cz.fei.upce.checkman.conf.redis

import com.fasterxml.jackson.databind.json.JsonMapper
import cz.fei.upce.checkman.domain.course.CourseSemesterAccessRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class CourseSemesterRequestConfiguration {
    @Bean
    fun redisOperations(factory: ReactiveRedisConnectionFactory): ReactiveRedisOperations<String, CourseSemesterAccessRequest> {
        val serializer = Jackson2JsonRedisSerializer(CourseSemesterAccessRequest::class.java)
        serializer.setObjectMapper(JsonMapper.builder()
            .findAndAddModules()
            .build()
        )

        val builder = RedisSerializationContext.newSerializationContext<String, CourseSemesterAccessRequest>(StringRedisSerializer())
        val context = builder.value(serializer).build()

        return ReactiveRedisTemplate(factory, context)
    }
}