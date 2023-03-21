package cz.fei.upce.checkman.controller.course

import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_OFFSET
import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_SIZE
import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.graphql.input.course.CourseRequirementsInputQL
import cz.fei.upce.checkman.graphql.input.course.SemesterInputQL
import cz.fei.upce.checkman.graphql.output.challenge.solution.statistic.FeedbackStatisticsQL
import cz.fei.upce.checkman.graphql.output.course.CourseQL
import cz.fei.upce.checkman.graphql.output.course.CourseRequirementsQL
import cz.fei.upce.checkman.graphql.output.course.CourseSemesterQL
import cz.fei.upce.checkman.graphql.output.course.CourseSemesterRoleQL
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import cz.fei.upce.checkman.service.course.CourseServiceV1
import cz.fei.upce.checkman.service.course.SemesterServiceV1
import cz.fei.upce.checkman.service.course.security.CourseAuthorizationServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.fei.upce.checkman.service.course.security.annotation.SemesterId
import cz.fei.upce.checkman.service.role.CourseSemesterRoleServiceV1
import org.springframework.data.domain.Sort
import org.springframework.graphql.data.method.annotation.*
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class CourseSemesterQLController(
    private val courseService: CourseServiceV1,
    private val courseSemesterRoleService: CourseSemesterRoleServiceV1,
    private val semesterService: SemesterServiceV1,
    private val courseAuthorizationService: CourseAuthorizationServiceV1,
    private val authenticationService: AuthenticationServiceV1
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
        return courseSemesterRoleService.addRole(appUserId, roleId, semesterId)
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
    fun createSemesterAccessRequest(@Argument semesterId: Long, authentication: Authentication) =
        courseAuthorizationService.createCourseAccessRequest(
            authenticationService.extractAuthenticateUser(
                authentication
            ), semesterId
        )

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
}