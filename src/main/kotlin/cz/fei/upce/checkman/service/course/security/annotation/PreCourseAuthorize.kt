package cz.fei.upce.checkman.service.course.security.annotation

import cz.fei.upce.checkman.domain.course.CourseSemesterRole
import java.lang.annotation.Inherited

@Target(AnnotationTarget.FUNCTION)
@Inherited
annotation class PreCourseAuthorize(
    val value: Array<CourseSemesterRole.Value> = [CourseSemesterRole.Value.COURSE_ROLE_ACCESS],
)
