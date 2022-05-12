package cz.fei.upce.checkman.dto

/**
 * Base interface for request with additional methods that should be implements on every DTO.
 *
 * @param D Request DTO which will implement this interface.
 * @param R Response DTO for this request DTO.
 * */
interface RequestDto<D, R> {
    fun toResponseDto(): R
    fun preventNullCollections(): D
}