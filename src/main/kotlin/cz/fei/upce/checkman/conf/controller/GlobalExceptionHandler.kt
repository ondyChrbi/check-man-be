package cz.fei.upce.checkman.conf.controller

import cz.jirutka.rsql.parser.RSQLParserException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.r2dbc.BadSqlGrammarException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import reactor.core.publisher.Mono

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BadSqlGrammarException::class)
    fun handleBadSqlGrammarException(ex: BadSqlGrammarException) =
        Mono.just(ResponseEntity.badRequest().body(ex.message))

    @ExceptionHandler(RSQLParserException::class)
    fun handleRSQLParserException(ex: RSQLParserException) =
        Mono.just(ResponseEntity.badRequest().body(ex.message))

    @ExceptionHandler(MalformedJwtException::class)
    fun handleMalformedJwtException(ex: MalformedJwtException) =
        Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED))

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwtException(ex: ExpiredJwtException) =
        Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED))
}