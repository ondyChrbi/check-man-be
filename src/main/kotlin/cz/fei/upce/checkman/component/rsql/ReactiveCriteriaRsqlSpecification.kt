package cz.fei.upce.checkman.component.rsql

import cz.jirutka.rsql.parser.RSQLParser
import cz.jirutka.rsql.parser.ast.*
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Criteria.empty
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.CriteriaDefinition
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Query.query
import org.springframework.data.relational.core.query.isIn
import org.springframework.stereotype.Component

@Component
class ReactiveCriteriaRsqlSpecification(private val rsqlParser: RSQLParser) {
    fun createCriteria(search: String): Query {
        return query(createCriteria(rsqlParser.parse(search)))
    }

    private fun createCriteria(node: Node): CriteriaDefinition {
        return when (node) {
            is LogicalNode -> createCriteria(node)
            is ComparisonNode -> createCriteria(node)
            else -> CriteriaDefinition.empty()
        }
    }

    private fun createCriteria(node: LogicalNode): CriteriaDefinition {
        val specs = node.children
            .mapNotNull { createCriteria(it) }
            .toList()

        var result = specs[0]
        if (result is Criteria) {
            when (node.operator) {
                LogicalOperator.AND -> specs.filter { it == result }.forEach { result = (result as Criteria).and(it) }
                LogicalOperator.OR -> specs.filter { it == result }.forEach { result = (result as Criteria).or(it) }
                null -> result = specs[0]
            }
        }

        return result
    }

    private fun createCriteria(node: ComparisonNode): CriteriaDefinition {
        return when (node.operator) {
            RSQLOperators.EQUAL -> where(node.selector).`is`(node.arguments.first())
            RSQLOperators.NOT_EQUAL -> where(node.selector).not(node.arguments.first())
            RSQLOperators.GREATER_THAN -> where(node.selector).greaterThan(node.arguments.first())
            RSQLOperators.GREATER_THAN_OR_EQUAL -> where(node.selector).greaterThanOrEquals(node.arguments.first())
            RSQLOperators.LESS_THAN -> where(node.selector).lessThan(node.arguments.first())
            RSQLOperators.LESS_THAN_OR_EQUAL -> where(node.selector).lessThanOrEquals(node.arguments.first())
            RSQLOperators.IN -> where(node.selector).isIn(node.arguments)
            RSQLOperators.NOT_IN -> where(node.selector).notIn(node.arguments)
            RSQLAdditionalOperators.LIKE -> where(node.selector).like(node.arguments.first())
            else -> empty()
        }
    }
}