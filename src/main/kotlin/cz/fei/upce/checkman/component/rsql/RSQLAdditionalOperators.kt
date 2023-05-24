package cz.fei.upce.checkman.component.rsql

import cz.jirutka.rsql.parser.ast.ComparisonOperator


object RSQLAdditionalOperators {
    val IS_EMPTY = ComparisonOperator("=isEmpty=", false)
    val LIKE = ComparisonOperator("=like=", false)
}