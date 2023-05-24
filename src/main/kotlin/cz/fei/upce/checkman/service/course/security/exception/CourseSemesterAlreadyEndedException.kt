package cz.fei.upce.checkman.service.course.security.exception

import cz.fei.upce.checkman.domain.course.Semester
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CourseSemesterAlreadyEndedException(semester: Semester, dateEnd: LocalDateTime, dateNow: LocalDateTime = LocalDateTime.now()) : Throwable(
    """
        Semester ${semester.id} for course ${semester.courseId} already ended. 
        End: ${dateEnd.format(DateTimeFormatter.ISO_DATE_TIME)} 
        Now: ${dateNow.format(DateTimeFormatter.ISO_DATE_TIME)}
    """
)