package cz.fei.upce.checkman.service.course

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.component.rsql.ReactiveCriteriaRSQLSpecification
import cz.fei.upce.checkman.domain.course.Course
import cz.fei.upce.checkman.domain.course.Semester
import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_COURSE_MANAGE
import cz.fei.upce.checkman.domain.user.GlobalRole.Companion.ROLE_COURSE_VIEW
import cz.fei.upce.checkman.dto.course.CourseRequestDtoV1
import cz.fei.upce.checkman.dto.course.CourseResponseDtoV1
import cz.fei.upce.checkman.dto.course.CourseSemesterRequestDtoV1
import cz.fei.upce.checkman.dto.course.CourseSemesterResponseDtoV1
import cz.fei.upce.checkman.dto.graphql.input.course.CourseInputQL
import cz.fei.upce.checkman.repository.course.AppUserCourseSemesterRoleRepository
import cz.fei.upce.checkman.repository.course.CourseRepository
import cz.fei.upce.checkman.repository.course.CourseRequirementsRepository
import cz.fei.upce.checkman.repository.course.SemesterRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import cz.fei.upce.checkman.service.course.security.exception.NotAssociatedSemesterWithCourseException
import org.springframework.data.domain.Sort.Order.desc
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Query.query
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.GroupedFlux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

import org.springframework.data.domain.Sort.by

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val semesterRepository: SemesterRepository,
    private val courseRequirementsRepository: CourseRequirementsRepository,
    private val appUserCourseSemesterRoleRepository: AppUserCourseSemesterRoleRepository,
    private val entityTemplate: R2dbcEntityTemplate,
    private val reactiveCriteriaRSQLSpecification: ReactiveCriteriaRSQLSpecification
) {
    fun search(search: String?): Flux<CourseResponseDtoV1> {
        val courses = if (search == null || search.isEmpty())
            courseRepository.findAll()
        else
            entityTemplate.select(Course::class.java)
                .matching(reactiveCriteriaRSQLSpecification.createCriteria(search))
                .all()

        return courses.map { CourseResponseDtoV1.fromEntity(it) }
            .flatMap { assignSemesters(it) }
    }

    fun existById(id: Long): Mono<Boolean> {
        return courseRepository.existsById(id)
    }

    fun findBySolutionId(solutionId: Long) : Mono<Course> {
        return courseRepository.findFirstBySolutionId(solutionId)
    }

    fun findAllAsQL(
        pageSize: Int? = CheckManApplication.DEFAULT_PAGE_SIZE,
        page: Int? = CheckManApplication.DEFAULT_PAGE,
    ): Flux<cz.fei.upce.checkman.dto.graphql.output.course.CourseQL> {

        return courseRepository.findAllPageable(pageSize ?: CheckManApplication.DEFAULT_PAGE_SIZE, page ?: CheckManApplication.DEFAULT_PAGE)
            .map { it.toQL() }
    }

    fun findAsDto(id: Long): Mono<CourseResponseDtoV1> {
        return courseRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { CourseResponseDtoV1.fromEntity(it) }
            .flatMap { assignSemesters(it) }
    }

    fun findAsQL(id: Long): Mono<cz.fei.upce.checkman.dto.graphql.output.course.CourseQL> {
        return courseRepository.findById(id).map { it.toQL() }
    }

    fun add(courseDto: CourseRequestDtoV1): Mono<CourseResponseDtoV1> = add(courseDto.toResponseDto())

    fun add(input: cz.fei.upce.checkman.dto.graphql.input.course.CourseInputQL): Mono<cz.fei.upce.checkman.dto.graphql.output.course.CourseQL> {
        return courseRepository.save(input.toEntity())
            .flatMap { course ->
                semesterRepository.saveAll(input.semesters.map { it.toEntity(course.id!!) })
                    .collectList()
                    .map { semester -> course.toQL(semester.map { it.toQL() }) }
            }
    }

    fun add(courseDto: CourseResponseDtoV1): Mono<CourseResponseDtoV1> {
        return courseRepository.save(courseDto.toEntity())
            .map { courseDto.withId(it.id) }
            .flatMap { saveSemesters(it) }
            .map { courseDto.withSemesters(it) }

    }

    fun update(courseId: Long, courseDto: CourseRequestDtoV1): Mono<CourseResponseDtoV1> {
        return update(courseId, courseDto.toResponseDto())
    }

    fun update(courseId: Long, courseDto: CourseResponseDtoV1): Mono<CourseResponseDtoV1> {
        return courseRepository.findById(courseId)
            .map { courseDto.toEntity(it) }
            .flatMap { courseRepository.save(it) }
            .map { courseDto.withId(it.id) }
    }

    fun delete(courseId: Long): Mono<Boolean> {
        val exist = courseRepository.existsById(courseId)

        return exist.flatMap {
            if (!it) {
                Mono.error(ResourceNotFoundException())
            } else {
                courseRepository.deleteById(courseId).thenReturn(true)
            }
        }
    }

    fun searchSemesters(search: String?, courseId: Long): Flux<CourseSemesterResponseDtoV1> {
        val semesters = if (search == null || search.isEmpty())
            semesterRepository.findAll()
        else
            entityTemplate.select(Semester::class.java)
                .matching(reactiveCriteriaRSQLSpecification.createCriteria(search))
                .all()

        return semesters.map { CourseSemesterResponseDtoV1.fromEntity(it) }
    }

    fun findSemester(courseId: Long, semesterId: Long): Mono<CourseSemesterResponseDtoV1> {
        return semesterRepository.findById(semesterId)
            .flatMap { checkCourseSemesterAssociation(courseId, it) }
            .map { CourseSemesterResponseDtoV1.fromEntity(it) }
    }

    fun addSemester(courseId: Long, courseSemesterDto: CourseSemesterRequestDtoV1): Mono<CourseSemesterResponseDtoV1> {
        return addSemester(courseId, courseSemesterDto.toResponseDto())
    }

    fun addSemester(
        courseId: Long, courseSemesterDtoV1: CourseSemesterResponseDtoV1
    ): Mono<CourseSemesterResponseDtoV1> {
        return courseRepository.findById(courseId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { semesterRepository.save(courseSemesterDtoV1.toEntity(courseId)) }
            .map { courseSemesterDtoV1.withId(it.id) }
    }

    fun updateSemester(courseId: Long, semesterId: Long, courseSemesterDto: CourseSemesterRequestDtoV1) =
        updateSemester(courseId, semesterId, courseSemesterDto.toResponseDto())

    fun updateSemester(
        courseId: Long, semesterId: Long,
        courseSemesterDto: CourseSemesterResponseDtoV1
    ): Mono<CourseSemesterResponseDtoV1> {
        return semesterRepository.findFirstByIdEqualsAndCourseIdEquals(semesterId, courseId)
            .switchIfEmpty(Mono.error(NotAssociatedSemesterWithCourseException(semesterId, courseId)))
            .map { courseSemesterDto.toEntity(it) }
            .flatMap { semesterRepository.save(it) }
            .map { courseSemesterDto.withId(it.id) }
    }

    fun deleteSemester(courseId: Long, semesterId: Long): Mono<Void> {
        return semesterRepository.findFirstByIdEqualsAndCourseIdEquals(semesterId, courseId)
            .switchIfEmpty(Mono.error(NotAssociatedSemesterWithCourseException(semesterId, courseId)))
            .flatMap { semesterRepository.deleteById(it.id!!) }
    }

    fun findAllRelatedToAsDto(appUser: AppUser): Flux<CourseResponseDtoV1> {
        return findAllRelatedTo(appUser)
            .flatMap { groupSemestersByCourseAsDto(it.key(), it.collectList()) }
    }

    fun findAllRelatedToAsQL(appUser: AppUser): Flux<cz.fei.upce.checkman.dto.graphql.output.course.CourseQL> {
        return findAllRelatedTo(appUser)
            .flatMap { groupSemestersByCourseAsQL(it.key(), it.collectList()) }
    }

    fun findAvailableToAsDto(appUser: AppUser): Flux<CourseResponseDtoV1> {
        return semesterRepository.findAllAvailableToAppUser(LocalDateTime.now(), appUser.id!!)
            .groupBy { it.courseId!! }
            .flatMap { groupSemestersByCourseAsDto(it.key(), it.collectList()) }
    }

    fun findAvailableToAsQL(appUser: AppUser): Flux<cz.fei.upce.checkman.dto.graphql.output.course.CourseQL> {
        return this.findAllAvailableToAppUser(appUser, LocalDateTime.now())
            .groupBy { it.courseId!! }
            .flatMap { groupSemestersByCourseAsQL(it.key(), it.collectList()) }
    }

    fun findSemesterAsQL(id: Long): Mono<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterQL> {
        return semesterRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { it.toQL() }
    }

    fun editRequirementsAsQL(semesterId: Long, input: cz.fei.upce.checkman.dto.graphql.input.course.CourseRequirementsInputQL) : Mono<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterQL> {
        return semesterRepository.findById(semesterId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { courseSemester -> saveCourseSemesterRequirements(semesterId, input, courseSemester) }
            .map { it.toQL() }
    }

    fun findSemesterRequirements(semesterId: Long): Mono<cz.fei.upce.checkman.dto.graphql.output.course.CourseRequirementsQL> {
        return courseRequirementsRepository.findFirstByCourseSemesterIdEquals(semesterId)
            .map { it.toDto() }
    }

    private fun findAllAvailableToAppUser(appUser: AppUser, currentDateTime: LocalDateTime): Flux<Semester> {
        return this.findAllRelatedToAsQL(appUser)
            .collectList()
            .flatMapMany { relatedCourses ->
                this.entityTemplate.select(Semester::class.java)
                    .matching(query(where("date_start").lessThanOrEquals(currentDateTime)
                        .and("date_end").greaterThanOrEquals(currentDateTime)
                        .and("id").notIn(relatedCourses.map { it.id }))
                        .sort(by(desc("date_start")))
                    )
                    .all()
            }
    }

    private fun groupSemestersByCourseAsDto(
        courseId: Long,
        semesters: Mono<List<Semester>>
    ): Mono<CourseResponseDtoV1> {
        return semesters.flatMap { semesters ->
            courseRepository.findById(courseId)
                .map { CourseResponseDtoV1.fromEntity(it, semesters) }
        }
    }

    private fun groupSemestersByCourseAsQL(
        courseId: Long,
        semesters: Mono<List<Semester>>
    ): Mono<cz.fei.upce.checkman.dto.graphql.output.course.CourseQL> {
        return semesters.flatMap { semesters ->
            courseRepository.findById(courseId)
                .map { course -> course.toQL(semesters.map { it.toQL() }) }
        }
    }

    private fun assignSemesters(courseDto: CourseResponseDtoV1): Mono<CourseResponseDtoV1> {
        return semesterRepository.findAllByCourseIdEquals(courseDto.id!!)
            .map { CourseSemesterResponseDtoV1.fromEntity(it) }
            .collectList()
            .map { courseDto.withSemesters(it) }
    }

    private fun assignSemesters(course: Course): Mono<cz.fei.upce.checkman.dto.graphql.output.course.CourseQL> {
        return semesterRepository.findAllByCourseIdEquals(course.id!!)
            .map { it.toQL() }
            .collectList()
            .map { course.toQL(it) }
    }

    private fun findAllRelatedTo(appUser: AppUser): Flux<GroupedFlux<Long, Semester>> {
        return appUserCourseSemesterRoleRepository.findDistinctByAppUserIdEqualsAndCourseSemesterRoleIdEquals(appUser.id!!, CourseSemesterRole.Value.ACCESS.id)
            .flatMap { semesterRepository.findById(it.courseSemesterId) }
            .groupBy { it.courseId!! }
    }

    private fun saveCourseSemesterRequirements(semesterId: Long, input: cz.fei.upce.checkman.dto.graphql.input.course.CourseRequirementsInputQL, semester: Semester?): Mono<Semester> =
        courseRequirementsRepository.deleteAllByCourseSemesterIdEquals(semesterId)
            .flatMap { courseRequirementsRepository.save(input.toEntity(semesterId)) }
            .mapNotNull { semester }

    private fun saveSemesters(courseDto: CourseResponseDtoV1): Mono<MutableList<CourseSemesterResponseDtoV1>> {
        return semesterRepository.saveAll(
            courseDto.semesters.map { it.toEntity(courseDto) }
        ).map { CourseSemesterResponseDtoV1.fromEntity(it) }.collectList()
    }

    fun edit(id: Long, input: CourseInputQL): Mono<Course> {
        return courseRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { it.update(input.toEntity()) }
            .flatMap { courseRepository.save(it) }
    }

    companion object {
        val VIEW_PERMISSIONS = setOf(ROLE_COURSE_MANAGE, ROLE_COURSE_VIEW)

        private fun checkCourseSemesterAssociation(courseId: Long, semester: Semester) =
            if (courseId != semester.courseId)
                Mono.error(NotAssociatedSemesterWithCourseException(courseId, semester.id!!))
            else
                Mono.just(semester)
    }
}
