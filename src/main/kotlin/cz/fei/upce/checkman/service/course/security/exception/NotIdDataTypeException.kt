package cz.fei.upce.checkman.service.course.security.exception

class NotIdDataTypeException(argName: String, expected: Class<*>) :
    Throwable("Id of $argName is not data type of ${expected.declaringClass.canonicalName}")