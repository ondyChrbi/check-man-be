package cz.fei.upce.checkman.repository

import cz.fei.upce.checkman.domain.Course
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CourseRepository : ReactiveCrudRepository<Course, Long>