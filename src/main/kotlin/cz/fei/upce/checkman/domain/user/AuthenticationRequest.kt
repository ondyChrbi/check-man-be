package cz.fei.upce.checkman.domain.user

import java.util.UUID

data class AuthenticationRequest (
    var id: String = UUID.randomUUID().toString(),
    var redirectUri: String = "",
    var appUser: AppUser? = null
) {
    fun getRedisKey() = getRedisKey(id)

    companion object {
        fun getRedisKey(id: String) = "${this::class.java}-${id}"
    }
}
