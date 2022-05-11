package cz.fei.upce.checkman.repository.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CourseSemesterRepository : ReactiveCrudRepository<CourseSemester, Long> {
    fun findFirstByIdEqualsAndCourseIdEquals(courseSemesterId: Long, courseId: Long) : Mono<CourseSemester>
}