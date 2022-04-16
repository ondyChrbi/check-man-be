package cz.fei.upce.checkman.repository.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CourseSemesterRepository : ReactiveCrudRepository<CourseSemester, Long>