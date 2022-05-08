package cz.fei.upce.checkman.dto

interface BaseDto<E, D> {
    fun withId(id: Long?): D
    fun toEntity(): E
}