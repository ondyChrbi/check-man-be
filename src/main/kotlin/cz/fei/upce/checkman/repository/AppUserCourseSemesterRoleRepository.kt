package cz.fei.upce.checkman.repository

import cz.fei.upce.checkman.domain.AppUserCourseSemesterRole
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface AppUserCourseSemesterRoleRepository : ReactiveCrudRepository<AppUserCourseSemesterRole, Long>