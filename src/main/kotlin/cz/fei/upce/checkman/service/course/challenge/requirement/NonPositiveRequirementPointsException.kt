package cz.fei.upce.checkman.service.course.challenge.requirement

class NonPositiveRequirementPointsException(points: Short) : Throwable("Non positive value was set: $points")