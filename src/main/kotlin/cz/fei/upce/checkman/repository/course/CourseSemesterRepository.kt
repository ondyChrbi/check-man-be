package cz.fei.upce.checkman.repository.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
interface CourseSemesterRepository : ReactiveCrudRepository<CourseSemester, Long> {
    fun findFirstByIdEqualsAndCourseIdEquals(courseSemesterId: Long, courseId: Long): Mono<CourseSemester>
    fun findAllByCourseIdEquals(courseId: Long): Flux<CourseSemester>
    fun existsByIdEqualsAndCourseIdEquals(courseSemesterId: Long, courseId: Long): Mono<Boolean>

    @Query(
        """
        select cs.* from course_semester cs
        where :currentDate between cs.date_start and cs.date_end
        """
    )
    fun findAllAvailableToAppUser(currentDate: LocalDateTime, appUserId: Long): Flux<CourseSemester>
}