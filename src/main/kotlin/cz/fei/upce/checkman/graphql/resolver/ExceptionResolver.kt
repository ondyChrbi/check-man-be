package cz.fei.upce.checkman.graphql.resolver

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.execution.DataFetcherExceptionResolver
import org.springframework.graphql.execution.ErrorType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ExceptionResolver : DataFetcherExceptionResolver {
    override fun resolveException(
        exception: Throwable,
        environment: DataFetchingEnvironment
    ): Mono<MutableList<GraphQLError>> {
        return Mono.just(
            mutableListOf(
                GraphqlErrorBuilder.newError(environment)
                    .message(exception.message)
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .build()
            )
        )
    }
}