package cz.fei.upce.checkman.service.course

import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_OFFSET
import cz.fei.upce.checkman.CheckManApplication.Companion.DEFAULT_SIZE
import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.repository.course.CourseSemesterRepository
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
    private val courseSemesterRepository: CourseSemesterRepository,
    private val feedbackStatisticsRepository: FeedbackStatisticsRepository,
) {

    fun findById(courseId: Long, semesterId: Long): Mono<CourseSemester> {
        return courseSemesterRepository.findFirstByIdEqualsAndCourseIdEquals(semesterId, courseId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
    }

    fun existById(courseId: Long, semesterId: Long): Mono<Boolean> {
        return courseSemesterRepository.existsByIdEqualsAndCourseIdEquals(semesterId, courseId)
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
        sortBy: CourseSemester.OrderByField = CourseSemester.OrderByField.id,
    ): Flux<List<CourseSemester>> {
        val sort = Sort.by(sortBy.toString())

        return Flux.fromIterable(coursesQL)
            .flatMapSequential { course ->
                courseSemesterRepository.findAllByCourseIdEquals(course.id!!, sort)
                    .collectList()
                    .defaultIfEmpty(listOf<CourseSemester>())
            }
    }

    fun findAllByCoursesQL(
        courseId: Long,
        oderBy: CourseSemester.OrderByField? = CourseSemester.OrderByField.id,
        sortOrder: Sort.Direction? = Sort.Direction.ASC,
        pageSize: Int? = DEFAULT_SIZE,
        page: Int? = DEFAULT_OFFSET,
    ): Flux<CourseSemester> {
        val sortField = oderBy ?: CourseSemester.OrderByField.id
        val sort = Sort.by(Sort.Order(sortOrder ?: Sort.Direction.ASC, sortField.toString()))
        val pageable = PageRequest.of(page ?: DEFAULT_OFFSET, pageSize ?: DEFAULT_SIZE)

        return courseSemesterRepository.findAllByCourseIdEquals(courseId, sort, pageable)
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
            courseSemesterRepository.save(input.toEntity(courseId))
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
        return courseSemesterRepository.existsById(id)
    }

    fun checkExistById(id: Long): Mono<Boolean> {
        return existById(id).map {
            if (!it)
                throw ResourceNotFoundException()
            it
        }
    }

    fun findById(id: Long): Mono<CourseSemester> {
        return courseSemesterRepository.findById(id)
    }

    fun findAllByUserHasRolesInCourse(courseId: Long, appUserId: Long, requestedRoles: List<Long> = listOf()): Flux<CourseSemester> {
        return courseSemesterRepository.findAllByUserHasRolesInCourse(courseId, appUserId, requestedRoles)
    }

    private companion object {
        const val DEFAULT_LIMIT = 5
    }
}
