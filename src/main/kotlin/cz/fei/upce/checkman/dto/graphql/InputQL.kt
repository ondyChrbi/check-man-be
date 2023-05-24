package cz.fei.upce.checkman.dto.graphql

interface InputQL<T> {
    fun toEntity(): T
}
