package cz.fei.upce.checkman.repository.course

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CourseSemesterRoleRepository : ReactiveCrudRepository<CourseSemesterRole, Long>