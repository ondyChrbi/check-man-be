package cz.fei.upce.checkman.service.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import cz.fei.upce.checkman.dto.course.CourseRequestDtoV1
import cz.fei.upce.checkman.dto.course.CourseResponseDtoV1
import cz.fei.upce.checkman.dto.course.CourseSemesterRequestDtoV1
import cz.fei.upce.checkman.dto.course.CourseSemesterResponseDtoV1
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
    fun find(id: Long): Mono<CourseResponseDtoV1> {
        return courseRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .map { CourseResponseDtoV1.fromEntity(it) }
            .flatMap { assignSemesters(it) }
    }

    fun add(courseDto: CourseRequestDtoV1): Mono<CourseResponseDtoV1> = add(courseDto.toResponseDto())

    fun add(courseDto: CourseResponseDtoV1): Mono<CourseResponseDtoV1> {
        return courseRepository.save(courseDto.toEntity())
            .map { courseDto.withId(it.id) }
            .flatMap { saveSemesters(it) }
            .map { courseDto.withSemesters(it) }

    }

    fun update(courseId: Long, courseDto: CourseRequestDtoV1) = update(courseId, courseDto.toResponseDto())

    fun update(courseId: Long, courseDto: CourseResponseDtoV1): Mono<CourseResponseDtoV1> {
        return courseRepository.findById(courseId)
            .map { courseDto.toEntity(it) }
            .flatMap { courseRepository.save(it) }
            .map { courseDto.withId(it.id) }
    }

    fun delete(courseId: Long) = courseRepository.deleteById(courseId)

    fun findSemester(courseId: Long, semesterId: Long): Mono<CourseSemesterResponseDtoV1> {
        return courseSemesterRepository.findById(semesterId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { checkCourseSemesterAssociation(courseId, it) }
            .map { CourseSemesterResponseDtoV1.fromEntity(it) }
    }

    fun addSemester(courseId: Long, courseSemesterDto: CourseSemesterRequestDtoV1) =
        addSemester(courseId, courseSemesterDto.toResponseDto())

    fun addSemester(
        courseId: Long,
        courseSemesterDtoV1: CourseSemesterResponseDtoV1
    ): Mono<CourseSemesterResponseDtoV1> {
        return courseRepository.findById(courseId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException()))
            .flatMap { courseSemesterRepository.save(courseSemesterDtoV1.toEntity()) }
            .map { courseSemesterDtoV1.withId(it.id) }
    }

    fun updateSemester(courseId: Long, semesterId: Long, courseSemesterDto: CourseSemesterRequestDtoV1) =
        updateSemester(courseId, semesterId, courseSemesterDto.toResponseDto())

    fun updateSemester(
        courseId: Long, semesterId: Long,
        courseSemesterDto: CourseSemesterResponseDtoV1
    ): Mono<CourseSemesterResponseDtoV1> {
        return courseSemesterRepository.findFirstByIdEqualsAndCourseIdEquals(semesterId, courseId)
            .switchIfEmpty(Mono.error(NotAssociatedSemesterWithCourseException(semesterId, courseId)))
            .map { courseSemesterDto.toEntity(it) }
            .flatMap { courseSemesterRepository.save(it) }
            .map { courseSemesterDto.withId(it.id) }
    }

    private fun saveSemesters(courseDto: CourseResponseDtoV1): Mono<MutableList<CourseSemesterResponseDtoV1>> {
        return courseSemesterRepository.saveAll(
            courseDto.semesters.map { it.toEntity(courseDto) }
        ).map { CourseSemesterResponseDtoV1.fromEntity(it) }.collectList()
    }

    fun deleteSemester(courseId: Long, semesterId: Long): Mono<Void> {
        return courseSemesterRepository.findFirstByIdEqualsAndCourseIdEquals(semesterId, courseId)
            .switchIfEmpty(Mono.error(NotAssociatedSemesterWithCourseException(semesterId, courseId)))
            .flatMap { courseSemesterRepository.deleteById(it.id!!) }
    }

    private fun assignSemesters(courseDto: CourseResponseDtoV1): Mono<CourseResponseDtoV1> {
        return courseSemesterRepository.findAllByCourseIdEquals(courseDto.id!!)
            .map { CourseSemesterResponseDtoV1.fromEntity(it) }
            .collectList()
            .map { courseDto.withSemesters(it) }
    }

    private companion object {
        fun checkCourseSemesterAssociation(courseId: Long, courseSemester: CourseSemester) =
            if (courseId != courseSemester.courseId)
                Mono.error(NotAssociatedSemesterWithCourseException(courseId, courseSemester.id!!))
            else
                Mono.just(courseSemester)
    }
}
