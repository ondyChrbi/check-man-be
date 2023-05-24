package cz.fei.upce.checkman.dto.appuser

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.dto.ResponseDto
import java.time.LocalDateTime

data class AppUserResponseDtoV1(
    var id: Long? = null,
    var stagId: String = "",
    var mail: String = "",
    var displayName: String = "",
    var registrationDate: LocalDateTime = LocalDateTime.now(),
    var lastAccessDate: LocalDateTime = LocalDateTime.now(),
    var disabled: Boolean = false,
    var globalRoles: Collection<GlobalRoleResponseDtoV1> = emptyList(),
    var courseRoles: Collection<CourseSemesterRolesResponseDtoV1> = emptyList()
) : ResponseDto<AppUser, AppUserResponseDtoV1>() {
    override fun withId(id: Long?): AppUserResponseDtoV1 {
        this.id = id
        return this
    }

    override fun toEntity() = AppUser(
        stagId = stagId, mail = mail, displayName = displayName,
        registrationDate = registrationDate, lastAccessDate = lastAccessDate, disabled = disabled
    )

    override fun toEntity(entity: AppUser): AppUser {
        entity.stagId = stagId
        entity.mail = mail
        entity.displayName = displayName
        entity.registrationDate = registrationDate
        entity.lastAccessDate = lastAccessDate
        entity.disabled = disabled

        return entity
    }

    fun withGlobalRoles(globalRoles: Collection<GlobalRoleResponseDtoV1>): AppUserResponseDtoV1{
        this.globalRoles = globalRoles

        return this
    }

    fun withCourseRoles(courseRoles: Collection<CourseSemesterRolesResponseDtoV1>): AppUserResponseDtoV1{
        this.courseRoles = courseRoles

        return this
    }

    companion object {
        fun fromEntity(appUser: AppUser) = AppUserResponseDtoV1(
            appUser.id, appUser.stagId, appUser.mail, appUser.displayName,
            appUser.registrationDate, appUser.lastAccessDate, appUser.disabled
        )
    }
}
