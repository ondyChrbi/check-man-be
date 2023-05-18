package cz.fei.upce.checkman.service.course

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_OFFSET
import cz.fei.upce.checkman.domain.course.Semester
import cz.fei.upce.checkman.dto.graphql.input.course.SemesterInputQL
import cz.fei.upce.checkman.repository.course.SemesterRepository
import cz.fei.upce.checkman.repository.review.statistic.FeedbackStatisticsRepository
import cz.fei.upce.checkman.repository.review.statistic.FeedbackStatisticsRepository.Companion.DEFAULT_PAGEABLE
import cz.fei.upce.checkman.repository.review.statistic.FeedbackStatisticsRepository.Companion.DEFAULT_SORT
import cz.fei.upce.checkman.service.ResourceNotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class SemesterService(
    private val courseService: CourseService,
    private val semesterRepository: SemesterRepository,
    private val feedbackStatisticsRepository: FeedbackStatisticsRepository,
) {

    fun findById(courseId: Long, semesterId: Long): Mono<Semester> {
        return semesterRepository.findFirstByIdEqualsAndCourseIdEquals(semesterId, courseId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
    }

    fun existById(courseId: Long, semesterId: Long): Mono<Boolean> {
        return semesterRepository.existsByIdEqualsAndCourseIdEquals(semesterId, courseId)
            .flatMap {
                if (!it) {
                    Mono.error(ResourceNotFoundException())
                } else {
                    Mono.just(it)
                }
            }
    }

    fun findAllByCoursesQL(
        coursesQL: List<cz.fei.upce.checkman.dto.graphql.output.course.CourseQL>,
        sortBy: Semester.OrderByField = Semester.OrderByField.id,
    ): Flux<List<Semester>> {
        val sort = Sort.by(sortBy.toString())

        return Flux.fromIterable(coursesQL)
            .flatMapSequential { course ->
                semesterRepository.findAllByCourseIdEquals(course.id!!, sort)
                    .collectList()
                    .defaultIfEmpty(listOf<Semester>())
            }
    }

    fun findAllByCoursesQL(
        courseId: Long,
        oderBy: Semester.OrderByField? = Semester.OrderByField.id,
        sortOrder: Sort.Direction? = Sort.Direction.ASC,
        pageSize: Int? = CheckManApplication.DEFAULT_LIMIT,
        page: Int? = DEFAULT_OFFSET,
    ): Flux<Semester> {
        val sortField = oderBy ?: Semester.OrderByField.id
        val sort = Sort.by(Sort.Order(sortOrder ?: Sort.Direction.ASC, sortField.toString()))
        val pageable = PageRequest.of(page ?: DEFAULT_OFFSET, pageSize ?: CheckManApplication.DEFAULT_LIMIT)

        return semesterRepository.findAllByCourseIdEquals(courseId, sort, pageable)
    }

    fun add(courseId: Long, input: cz.fei.upce.checkman.dto.graphql.input.course.SemesterInputQL): Mono<cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterQL> {
        val courseExistMono = courseService.existById(courseId)
            .flatMap {
                if (!it)
                    Mono.error(ResourceNotFoundException())
                else
                    Mono.just(it)
            }

        return courseExistMono.flatMap {
            semesterRepository.save(input.toEntity(courseId))
        }.map { it.toQL() }
    }

    fun makeStatistic(semesters: cz.fei.upce.checkman.dto.graphql.output.course.CourseSemesterQL): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.statistic.FeedbackStatisticsQL> {
        return feedbackStatisticsRepository.findDistinctBySemesterIdEquals(semesters.id)
            .map { it.toQL() }
    }

    fun findAllStatistics(
        semesterId: Long,
        order: Sort.Direction? = Sort.Direction.ASC,
        limit: Int? = DEFAULT_LIMIT,
        description: String?,
    ): Flux<cz.fei.upce.checkman.dto.graphql.output.challenge.solution.statistic.FeedbackStatisticsQL> {
        val sort = if (order != null) Sort.by(order, "count") else DEFAULT_SORT
        val pageable = if (limit != null) PageRequest.of(0, limit) else DEFAULT_PAGEABLE

        val resultFlux = if (description == null)
            feedbackStatisticsRepository.findDistinctBySemesterIdEquals(semesterId, sort, pageable)
        else
            feedbackStatisticsRepository.findDistinctBySemesterIdEqualsAndDescriptionContainingIgnoreCase(
                semesterId,
                description,
                sort,
                pageable
            )

        return resultFlux.map { it.toQL() }
    }

    fun existById(id: Long): Mono<Boolean> {
        return semesterRepository.existsById(id)
    }

    fun checkExistById(id: Long): Mono<Boolean> {
        return existById(id).map {
            if (!it)
                throw ResourceNotFoundException()
            it
        }
    }

    fun findById(id: Long): Mono<Semester> {
        return semesterRepository.findById(id)
    }

    fun findAllByUserHasRolesInCourse(courseId: Long, appUserId: Long, requestedRoles: List<Long> = listOf()): Flux<Semester> {
        return semesterRepository.findAllByUserHasRolesInCourse(courseId, appUserId, requestedRoles)
    }

    fun edit(id: Long, input: SemesterInputQL): Mono<Semester> {
        return semesterRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { it.update(input.toEntity()) }
            .flatMap { semesterRepository.save(it) }
    }

    fun delete(id: Long): Mono<Boolean> {
        val exist = semesterRepository.existsById(id)

        return exist.flatMap {
            if (!it) {
                Mono.error(ResourceNotFoundException())
            } else {
                semesterRepository.deleteById(id)
                    .then(Mono.just(true))
            }
        }
    }

    private companion object {
        const val DEFAULT_LIMIT = 5
    }
}
