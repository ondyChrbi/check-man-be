package cz.fei.upce.checkman.repository.course

import cz.fei.upce.checkman.domain.course.CourseRequirements
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CourseRequirementsRepository : ReactiveCrudRepository<CourseRequirements, Long> {
    fun findFirstByCourseSemesterIdEquals(courseSemesterId: Long) : Mono<CourseRequirements>

    fun deleteAllByCourseSemesterIdEquals(courseSemesterId: Long) : Mono<Boolean>
}