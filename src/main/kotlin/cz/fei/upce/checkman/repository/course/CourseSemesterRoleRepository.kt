package cz.fei.upce.checkman.repository.course

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface CourseSemesterRoleRepository : ReactiveCrudRepository<CourseSemesterRole, Long> {
    @Query("""
        select distinct csr.* from course_semester_role csr
        inner join app_user_course_semester_role aucsr on csr.id = aucsr.course_semester_role_id
        where aucsr.app_user_id = :appUserId and aucsr.course_semester_id = :courseSemesterId
    """)
    fun findAllRelatedToUserAndCourseSemester(appUserId: Long, courseSemesterId: Long) : Flux<CourseSemesterRole>
}