package cz.fei.upce.checkman.service.course

import cz.fei.upce.checkman.dto.course.CourseDtoV1
import cz.fei.upce.checkman.dto.course.CourseSemesterDtoV1
import cz.fei.upce.checkman.repository.course.CourseRepository
import cz.fei.upce.checkman.repository.course.CourseSemesterRepository
import org.springframework.stereotype.Service

@Service
class CourseServiceV1(
    private val courseRepository: CourseRepository,
    private val courseSemesterRepository: CourseSemesterRepository
) {
    fun add(courseDto: CourseDtoV1) = courseRepository.save(courseDto.toEntity())
        .map { courseDto.withId(it.id) }
        .flatMap { saveSemesters(it) }
        .map { courseDto.withSemesters(it) }

    private fun saveSemesters(courseDto: CourseDtoV1) =
        courseSemesterRepository.saveAll(
            courseDto.semesters.map { it.toEntity(courseDto) }
        ).map { CourseSemesterDtoV1.fromEntity(it) }.collectList()
}
