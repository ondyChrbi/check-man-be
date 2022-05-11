package cz.fei.upce.checkman.service.course

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotAssociatedSemesterWithCourseException(semesterId: Long, courseId: Long) : Throwable("Semester $semesterId is not exist or associated with course $courseId")