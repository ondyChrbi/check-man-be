package cz.fei.upce.checkman.dto

import org.springframework.hateoas.RepresentationModel
abstract class BaseDto<E, D : RepresentationModel<out D>?> : RepresentationModel<D>(){
    abstract fun withId(id: Long?): D
    abstract fun toEntity(): E
    abstract fun toEntity(entity: E): E
}