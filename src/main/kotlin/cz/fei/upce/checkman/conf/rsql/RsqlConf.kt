package cz.fei.upce.checkman.conf.rsql

import cz.fei.upce.checkman.component.rsql.RSQLAdditionalOperators.IS_EMPTY
import cz.fei.upce.checkman.component.rsql.RSQLAdditionalOperators.LIKE
import cz.jirutka.rsql.parser.RSQLParser
import cz.jirutka.rsql.parser.ast.RSQLOperators.defaultOperators
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RsqlConf {
    @Bean
    fun rsqlParser(): RSQLParser {
        val operators = defaultOperators()
        operators.addAll(listOf(IS_EMPTY, LIKE))

        return RSQLParser(operators)
    }
}