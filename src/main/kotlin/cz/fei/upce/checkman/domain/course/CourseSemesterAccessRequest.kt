package cz.fei.upce.checkman.domain.course

import cz.fei.upce.checkman.conf.redis.CacheKey
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.graphql.output.course.CourseSemesterAccessRequestQL
import java.time.LocalDateTime
import java.util.UUID

data class CourseSemesterAccessRequest(
    val appUser: AppUser,
    val semesterId: Long,
    val id: String = UUID.randomUUID().toString()
) : CacheKey {
    override fun toCacheKey() = "{${CourseSemesterAccessRequest::class.java.simpleName}}-$semesterId-${appUser.id}"

    override fun toAllCacheKeyPattern() = "{${CourseSemesterAccessRequest::class.java.simpleName}}-$semesterId-*"

    fun toQL(
        dateCreation: LocalDateTime = LocalDateTime.now(),
        expirationDate: LocalDateTime = LocalDateTime.now().plusSeconds(EXPIRATION)
    ): CourseSemesterAccessRequestQL {
        return CourseSemesterAccessRequestQL(appUser.toQL(), semesterId, dateCreation, expirationDate, id)
    }

    companion object {
        const val EXPIRATION = 60L
    }
}