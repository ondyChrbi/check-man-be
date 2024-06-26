package cz.fei.upce.checkman.service.course.security.exception

import cz.upce.fei.checkman.domain.course.security.annotation.CourseId

class NotCourseIdDataTypeException(expected: Class<*>) :
    Throwable("Id of ${CourseId::class.qualifiedName} is not data type of ${expected.declaringClass.canonicalName}")