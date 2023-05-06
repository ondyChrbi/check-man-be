package cz.fei.upce.checkman.domain.course

import cz.fei.upce.checkman.conf.redis.CacheKey
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterAccessRequestQL
import java.time.LocalDateTime
import java.util.UUID

data class CourseSemesterAccessRequest(
    val appUser: AppUser,
    val semesterId: Long,
    val id: String = UUID.randomUUID().toString(),
    val dateCreation: LocalDateTime = LocalDateTime.now(),
    val expirationDate: LocalDateTime = dateCreation.plusSeconds(EXPIRATION)
) : CacheKey {
    override fun toCacheKey() = "{${CourseSemesterAccessRequest::class.java.simpleName}}-$semesterId-${appUser.id}-$id"

    override fun toAllCacheKeyPattern() = "{${CourseSemesterAccessRequest::class.java.simpleName}}-$semesterId-*"

    fun toQL(
        dateCreation: LocalDateTime = LocalDateTime.now(),
        expirationDate: LocalDateTime = LocalDateTime.now().plusSeconds(EXPIRATION)
    ): cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterAccessRequestQL {
        return cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterAccessRequestQL(
            appUser.toQL(),
            semesterId,
            dateCreation,
            expirationDate,
            id
        )
    }

    fun toQL(): cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterAccessRequestQL {
        return cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterAccessRequestQL(
            appUser.toQL(),
            semesterId,
            dateCreation,
            expirationDate,
            id
        )
    }

    companion object {
        const val EXPIRATION = 60L

        fun cacheKeyPatternAppUser(appUserId: Long): String {
            return "{${CourseSemesterAccessRequest::class.java.simpleName}}-*-$appUserId-*"
        }

        fun cacheKeyPatternSemester(semesterId: Long): String {
            return "{${CourseSemesterAccessRequest::class.java.simpleName}}-$semesterId-*"
        }

        fun cacheKeyPatternId(id: String): String {
            return "{${CourseSemesterAccessRequest::class.java.simpleName}}-*-$id"
        }

        fun cacheKeyPattern(semesterId: Long, appUser: AppUser): String {
            return "{${CourseSemesterAccessRequest::class.java.simpleName}}-$semesterId-${appUser.id}-*"
        }

        fun cacheKeyPattern(semesterId: Long, appUserId: Long): String {
            return "{${CourseSemesterAccessRequest::class.java.simpleName}}-$semesterId-$appUserId-*"
        }
    }
}