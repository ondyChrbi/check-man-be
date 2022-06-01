package cz.fei.upce.checkman.service.course.security.exception

import cz.fei.upce.checkman.service.course.security.annotation.CourseId
import java.lang.reflect.Parameter

class NotOneCourseIdInMethodException(courses: List<Parameter>) : Throwable(
    if (courses.isEmpty())
        "Method does not contain ${CourseId::class.qualifiedName} annotation (add one to identify course primary key)."
    else
        "Method contains more than one ${CourseId::class.qualifiedName} annotation: {${
            courses.joinToString(", ") { it.name }
        }}. Leave only one who identify the primary key of course."
)
