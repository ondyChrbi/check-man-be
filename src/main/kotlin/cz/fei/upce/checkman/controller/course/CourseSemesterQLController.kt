package cz.fei.upce.checkman.controller.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.graphql.input.course.SemesterInputQL
import cz.fei.upce.checkman.graphql.output.course.CourseQL
import cz.fei.upce.checkman.graphql.output.course.CourseSemesterQL
import cz.fei.upce.checkman.graphql.output.course.CourseSemesterRoleQL
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import cz.fei.upce.checkman.service.course.CourseServiceV1
import cz.fei.upce.checkman.service.course.SemesterServiceV1
import cz.fei.upce.checkman.service.course.security.CourseAuthorizationServiceV1
import cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize
import cz.fei.upce.checkman.service.course.security.annotation.SemesterId
import cz.fei.upce.checkman.service.role.CourseSemesterRoleServiceV1
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.Authentication
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class CourseSemesterQLController(
    private val courseServiceV1: CourseServiceV1,
    private val courseSemesterRoleService: CourseSemesterRoleServiceV1,
    private val semesterService: SemesterServiceV1,
    private val courseAuthorizationService: CourseAuthorizationServiceV1,
    private val authenticationService: AuthenticationServiceV1
    ) {

    @BatchMapping(typeName = "semesters")
    fun semesters(courses: List<CourseQL>): Flux<List<CourseSemester>> {
        return semesterService.findAllByCoursesQL(courses)
    }

    @QueryMapping
    fun course(@Argument id: Long) = courseServiceV1.findAsQL(id)

    @QueryMapping
    @PreCourseSemesterAuthorize
    fun semester(@SemesterId @Argument id: Long, authentication: Authentication): Mono<CourseSemesterQL> {
        return courseServiceV1.findSemesterAsQL(id)
    }

    @MutationMapping
    fun createSemester(@Argument courseId : Long, @Argument input: SemesterInputQL, authentication: Authentication): Mono<CourseSemesterQL> {
        return semesterService.add(courseId, input)
    }

    @QueryMapping
    fun allCourseRoles() : Flux<CourseSemesterRoleQL> {
        return courseSemesterRoleService.findAllAsQL()
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.MANAGE_USERS])
    fun addCourseRole(@Argument appUserId: Long, @Argument @SemesterId semesterId: Long, @Argument roleId: Long, authentication: Authentication): Mono<Boolean> {
        return courseSemesterRoleService.addRole(appUserId, roleId, semesterId)
    }

    @MutationMapping
    @PreCourseSemesterAuthorize([CourseSemesterRole.Value.ACCESS, CourseSemesterRole.Value.MANAGE_USERS])
    fun removeCourseRole(@Argument appUserId: Long, @Argument @SemesterId semesterId: Long, @Argument roleId: Long, authentication: Authentication): Mono<Boolean> {
        return courseSemesterRoleService.removeRole(appUserId, roleId, semesterId)
    }

    @MutationMapping
    fun createSemesterAccessRequest(@Argument semesterId: Long, authentication: Authentication) =
        courseAuthorizationService.createCourseAccessRequest(authenticationService.extractAuthenticateUser(authentication), semesterId)
}