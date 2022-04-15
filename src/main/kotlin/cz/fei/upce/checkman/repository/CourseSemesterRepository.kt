package cz.fei.upce.checkman.repository

import cz.fei.upce.checkman.domain.CourseSemester
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CourseSemesterRepository : ReactiveCrudRepository<CourseSemester , Long>