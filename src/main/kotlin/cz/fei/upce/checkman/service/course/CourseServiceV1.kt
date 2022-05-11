package cz.fei.upce.checkman.service.course

import cz.fei.upce.checkman.dto.course.CourseDtoV1
import cz.fei.upce.checkman.dto.course.CourseSemesterDtoV1
import cz.fei.upce.checkman.repository.course.CourseRepository
import cz.fei.upce.checkman.repository.course.CourseSemesterRepository
import cz.fei.upce.checkman.service.ResourceNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CourseServiceV1(
    private val courseRepository: CourseRepository,
    private val courseSemesterRepository: CourseSemesterRepository
) {
    fun add(courseDto: CourseDtoV1) = courseRepository.save(courseDto.toEntity())
        .map { courseDto.withId(it.id) }
        .flatMap { saveSemesters(it) }
        .map { courseDto.withSemesters(it) }

    fun update(courseId: Long, courseDto: CourseDtoV1) = courseRepository.findById(courseId)
        .map { courseDto.toEntity(it) }
        .flatMap { courseRepository.save(it) }
        .map { courseDto.withId(it.id) }

    fun delete(courseId: Long) = courseRepository.deleteById(courseId)

    fun addSemester(courseId: Long, courseSemesterDtoV1: CourseSemesterDtoV1) = courseRepository.findById(courseId)
        .switchIfEmpty(Mono.error(ResourceNotFoundException()))
        .flatMap { courseSemesterRepository.save(courseSemesterDtoV1.toEntity()) }
        .map { courseSemesterDtoV1.withId(it.id) }

    fun updateSemester(courseId: Long, semesterId: Long, courseSemesterDto: CourseSemesterDtoV1): Mono<CourseSemesterDtoV1> {
        return courseSemesterRepository.findFirstByIdEqualsAndCourseIdEquals(semesterId, courseId)
            .switchIfEmpty(Mono.error(NotAssociatedSemesterWithCourseException(semesterId, courseId)))
            .map { courseSemesterDto.toEntity(it) }
            .flatMap { courseSemesterRepository.save(it) }
            .map { courseSemesterDto.withId(it.id) }
    }

    private fun saveSemesters(courseDto: CourseDtoV1) =
        courseSemesterRepository.saveAll(
            courseDto.semesters.map { it.toEntity(courseDto) }
        ).map { CourseSemesterDtoV1.fromEntity(it) }.collectList()

    fun deleteSemester(courseId: Long, semesterId: Long) = courseSemesterRepository.findFirstByIdEqualsAndCourseIdEquals(semesterId, courseId)
        .switchIfEmpty(Mono.error(NotAssociatedSemesterWithCourseException(semesterId, courseId)))
        .flatMap { courseSemesterRepository.deleteById(it.id!!) }
}
