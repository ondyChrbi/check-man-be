package cz.fei.upce.checkman.service.course.challenge.requirement

class MaximumRequirementPointsReachedException(points: Short, maxPoints: Short)
    : Throwable ("More than maximum points reached ($points/$maxPoints)")