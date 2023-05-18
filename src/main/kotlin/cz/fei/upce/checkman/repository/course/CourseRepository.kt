package cz.fei.upce.checkman.repository.course

import cz.fei.upce.checkman.CheckManApplication
import cz.fei.upce.checkman.domain.course.Course
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CourseRepository : ReactiveCrudRepository<Course, Long> {
    @Query("""
        select * from course c
        inner join course_semester cs on c.id = cs.course_id
        where cs.id = :semesterId
    """)
    fun findBySemesterIdEquals(semesterId: Long): Mono<Course>


    @Query("""
        select * from course c
        inner join course_semester cs on c.id = cs.course_id
        inner join challenge c2 on cs.id = c2.course_semester_id
        inner join solution s on c2.id = s.challenge_id
        where s.id = :solutionId
    """)
    fun findFirstBySolutionId(solutionId: Long): Mono<Course>

    @Query("""
        select * from course c
        limit :size offset :offset
    """)

    fun findAllPageable(size : Int = CheckManApplication.DEFAULT_LIMIT, offset : Int = CheckManApplication.DEFAULT_OFFSET): Flux<Course>
}