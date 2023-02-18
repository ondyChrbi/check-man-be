package cz.fei.upce.checkman.service.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.graphql.input.course.SemesterInputQL
import cz.fei.upce.checkman.graphql.output.course.CourseQL
import cz.fei.upce.checkman.graphql.output.course.CourseSemesterQL
import cz.fei.upce.checkman.repository.course.CourseSemesterRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class SemesterServiceV1(
    private val courseService: CourseServiceV1,
    private val courseSemesterRepository: CourseSemesterRepository
) {
    fun findAllByCoursesQL(coursesQL: List<CourseQL>): Flux<List<CourseSemester>> {
        return Flux.fromIterable(coursesQL)
            .flatMapSequential { course ->
                courseSemesterRepository.findAllByCourseIdEquals(course.id!!)
                    .collectList()
                    .defaultIfEmpty(listOf<CourseSemester>())
            }
    }

    fun add(courseId: Long, input: SemesterInputQL): Mono<CourseSemesterQL> {
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
}
