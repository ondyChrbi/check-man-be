package cz.fei.upce.checkman.service.course.security.exception

import cz.fei.upce.checkman.service.course.security.annotation.SemesterId

class NotSemesterIdDataTypeException(expected: Class<*>) :
    Throwable("Id of ${SemesterId::class.qualifiedName} is not data type of ${expected.declaringClass.canonicalName}")