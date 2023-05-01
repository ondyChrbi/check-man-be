package cz.fei.upce.checkman.controller.course

import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_OFFSET
import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_SIZE
import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.graphql.input.course.CourseRequirementsInputQL
import cz.fei.upce.checkman.graphql.input.course.SemesterInputQL
import cz.fei.upce.checkman.graphql.output.appuser.AppUserQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.statistic.FeedbackStatisticsQL
import cz.fei.upce.checkman.graphql.output.course.*
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceImpl
import cz.fei.upce.checkman.service.course.security.exception.AppUserCourseSemesterForbiddenException
import cz.fei.upce.checkman.service.course.CourseService
import cz.fei.upce.checkman.service.course.SemesterService
import cz.fei.upce.checkman.service.course.security.CourseAuthorizationService
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.upce.fei.checkman.domain.course.security.annotation.SemesterId
import cz.fei.upce.checkman.service.role.CourseSemesterRoleService
import org.springframework.data.domain.Sort
import org.springframework.graphql.data.method.annotation.*
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class CourseSemesterQLController(
    private val courseService: CourseService,
    private val courseSemesterRoleService: CourseSemesterRoleService,
    private val semesterService: SemesterService,
    private val courseAuthorizationService: CourseAuthorizationService,
    private val authenticationService: AuthenticationServiceImpl
) {

    @QueryMapping
    fun course(@Argument id: Long): Mono<CourseQL> {
        return courseService.findAsQL(id)
    }

    @BatchMapping(typeName = "Course")
    fun semesters(courses: List<CourseQL>): Flux<List<CourseSemester>> {
        return semesterService.findAllByCoursesQL(courses)
    }

    @QueryMapping
    @PreCourseSemesterAuthorize
    fun semester(@SemesterId @Argument id: Long, authentication: Authentication): Mono<CourseSemesterQL> {
        return courseService.findSemesterAsQL(id)
    }

    @QueryMapping("semesters")
    fun allSemesters(
        @Argument courseId: Long,
        @Argument oderBy: CourseSemester.OrderByField? = CourseSemester.OrderByField.id,
        @Argument sortOrder: Sort.Direction? = Sort.Direction.ASC,
        @Argument pageSize: Int? = DEFAULT_SIZE,
        @Argument page: Int? = DEFAULT_OFFSET,
    ): Flux<CourseSemester> {
        return semesterService.findAllByCoursesQL(courseId, oderBy, sortOrder, pageSize, page)
    }

    @MutationMapping
    fun createSemester(
        @Argument courseId: Long,
        @Argument input: SemesterInputQL,
        authentication: Authentication
    ): Mono<CourseSemesterQL> {
        return semesterService.add(courseId, input)
    }

    @QueryMapping
    fun allCourseRoles(): Flux<CourseSemesterRoleQL> {
        return courseSemesterRoleService.findAllAsQL()
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.MANAGE_USERS])
    fun addCourseRole(
        @Argument appUserId: Long,
        @Argument @SemesterId semesterId: Long,
        @Argument roleId: Long,
        authentication: Authentication
    ): Mono<Boolean> {
        return courseSemesterRoleService.addRole(appUserId, semesterId, roleId)
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.MANAGE_USERS])
    fun removeCourseRole(
        @Argument appUserId: Long,
        @Argument @SemesterId semesterId: Long,
        @Argument roleId: Long,
        authentication: Authentication
    ): Mono<Boolean> {
        return courseSemesterRoleService.removeRole(appUserId, roleId, semesterId)
    }

    @MutationMapping
    fun createSemesterAccessRequest(@Argument semesterId: Long, authentication: Authentication): Mono<CourseSemesterAccessRequestQL> {
        val appUser = authenticationService.extractAuthenticateUser(authentication)

        return courseAuthorizationService.createCourseAccessRequest(appUser, semesterId)
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.EDIT_COURSE])
    fun editSemesterRequirements(@Argument @SemesterId semesterId: Long, @Argument input : CourseRequirementsInputQL, authentication: Authentication): Mono<CourseSemesterQL> {
        return courseService.editRequirementsAsQL(semesterId, input)
    }

    @SchemaMapping(typeName = "Semester")
    fun fulfillmentConditions (semestersQL: CourseSemesterQL): Mono<CourseRequirementsQL> {
        return courseService.findSemesterRequirements(semestersQL.id)
    }

    @SchemaMapping(typeName = "Semester")
    fun statistic(semestersQL: CourseSemesterQL): Flux<FeedbackStatisticsQL> {
        return semesterService.makeStatistic(semestersQL)
    }

    @QueryMapping("statistic")
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.VIEW_STATISTICS])
    fun statisticQuery(@Argument @SemesterId semesterId: Long, @Argument direction : Sort.Direction?,
                       @Argument limit: Int?, @Argument description: String?,
                       authentication: Authentication): Flux<FeedbackStatisticsQL> {
        return semesterService.findAllStatistics(semesterId, direction, limit, description)
    }

    @QueryMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.MANAGE_USERS])
    fun semesterAccessRequests(@Argument @SemesterId semesterId: Long, authentication: Authentication) : Flux<CourseSemesterAccessRequestQL> {
        return courseAuthorizationService.findAllCourseAccessRequestsBySemester(semesterId)
    }

    @QueryMapping
    fun semesterAccessRequestsAppUser(@Argument @SemesterId semesterId: Long, @Argument appUserId: Long?, authentication: Authentication?): Mono<CourseSemesterAccessRequestQL> {
        val appUser = authenticationService.extractAuthenticateUser(authentication!!)

        return if (appUserId != null && appUserId != appUser.id) {
            courseSemesterRoleService.hasRole(appUser, semesterId, CourseSemesterRole.Value.MANAGE_USERS)
                .flatMap { checkPermission(it) }
                .flatMap { courseAuthorizationService.findAllCourseAccessRequests(appUserId, semesterId) }
        } else {
            courseAuthorizationService.findAllCourseAccessRequests(appUserId ?: appUser.id!!, semesterId)
        }
    }

    @MutationMapping
    fun approveCourseSemesterRequest(@Argument id: String, @Argument roles: MutableList<String>? = mutableListOf()) : Mono<Boolean> {
        val rolesToAdd = if (roles.isNullOrEmpty())
            listOf(CourseSemesterRole.Value.ACCESS)
        else
            roles.map { CourseSemesterRole.Value.valueOf(it) }

        return courseAuthorizationService.approveCourseSemesterRequest(id, rolesToAdd)
    }

    @SchemaMapping(typeName = "AppUser")
    fun accessRequests(appUser: AppUserQL): Flux<CourseSemesterAccessRequestQL> {
        return courseAuthorizationService.findAllCourseAccessRequestsBySemester(appUser)
    }

    private fun checkPermission(it: Boolean) = if (!it) {
        Mono.error(AppUserCourseSemesterForbiddenException())
    } else {
        Mono.just(true)
    }
}