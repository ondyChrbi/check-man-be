package cz.fei.upce.checkman.service.course.security.exception

import cz.fei.upce.checkman.domain.course.CourseSemester
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CourseSemesterNotStartedYetException(semester: CourseSemester, dateStart: LocalDateTime, dateNow: LocalDateTime = LocalDateTime.now()) : Throwable(
    """
        Semester ${semester.id} for course ${semester.courseId} not started yet. 
        Start: ${dateStart.format(DateTimeFormatter.ISO_DATE_TIME)} 
        Now: ${dateNow.format(DateTimeFormatter.ISO_DATE_TIME)}
    """
)
