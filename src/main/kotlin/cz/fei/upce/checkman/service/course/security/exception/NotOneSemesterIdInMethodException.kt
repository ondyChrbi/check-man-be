package cz.fei.upce.checkman.service.course.security.exception

import cz.fei.upce.checkman.service.course.security.annotation.SemesterId
import java.lang.reflect.Parameter

class NotOneSemesterIdInMethodException(semesters: List<Parameter>) : Throwable(
    if (semesters.isEmpty())
        "Method does not contain ${SemesterId::class.qualifiedName} annotation (add one to identify semester primary key)."
    else
        "Method contains more than  ${SemesterId::class.qualifiedName} annotation: {${
            semesters.joinToString(", ") { it.name }
        }}. Leave only one who identify the primary key of semester."
)
