package cz.fei.upce.checkman.repository.course

import cz.fei.upce.checkman.domain.course.CourseSemester
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CourseSemesterRepository : ReactiveCrudRepository<CourseSemester, Long>