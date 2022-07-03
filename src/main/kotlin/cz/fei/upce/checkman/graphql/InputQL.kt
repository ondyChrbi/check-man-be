package cz.fei.upce.checkman.graphql

interface InputQL<T> {
    fun toEntity(): T
}
