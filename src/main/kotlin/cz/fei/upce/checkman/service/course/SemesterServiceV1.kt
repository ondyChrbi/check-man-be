package cz.fei.upce.checkman.service.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.graphql.output.course.CourseQL
import cz.fei.upce.checkman.repository.course.CourseSemesterRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class SemesterServiceV1(
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
}
