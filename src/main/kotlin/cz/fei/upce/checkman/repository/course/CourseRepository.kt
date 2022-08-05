package cz.fei.upce.checkman.repository.course

import cz.fei.upce.checkman.domain.course.Course
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CourseRepository : ReactiveCrudRepository<Course, Long> {
    @Query("""
        select * from course c
        inner join course_semester cs on c.id = cs.course_id
        where cs.id = :semesterId
    """)
    fun findBySemesterIdEquals(semesterId: Long): Mono<Course>
}