package cz.fei.upce.checkman.service.course.security

import cz.fei.upce.checkman.domain.user.AppUser
import cz.fei.upce.checkman.repository.course.CourseSemesterRepository
import cz.fei.upce.checkman.service.authentication.AuthenticationServiceV1
import cz.fei.upce.checkman.service.course.AppUserCourseSemesterForbiddenException
import cz.fei.upce.checkman.service.course.security.annotation.*
import cz.fei.upce.checkman.service.course.security.exception.*
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.Authentication
import reactor.core.CorePublisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Suppress("ReactiveStreamsUnusedPublisher")
@Aspect
@Configuration
class CourseAccessProfilingAspect(
    private val courseSemesterRepository: CourseSemesterRepository,
    private val authorizeService: CourseAuthorizationServiceV1,
    private val authenticationService: AuthenticationServiceV1
) {
    @Around("@annotation(cz.fei.upce.checkman.service.course.security.annotation.PreCourseSemesterAuthorize)")
    fun checkCourseAccess(joinPoint: ProceedingJoinPoint): CorePublisher<out Any> {
        val methodSignature = joinPoint.signature as MethodSignature
        val returnType = methodSignature.returnType

        if (isNotReactiveReturnType(returnType)) {
            throw NotReactiveReturnTypeException(methodSignature)
        }

        val authentication : Authentication?
        try {
            authentication = joinPoint.args.first { it is Authentication } as Authentication
        } catch (e: NoSuchElementException) {
            throw MissingAuthenticationArgumentException(methodSignature)
        }

        val appUser = authenticationService.extractAuthenticateUser(authentication)

        val annotation = methodSignature.method.getAnnotation(PreCourseSemesterAuthorize::class.java)
        val parameters = methodSignature.method.parameters

        val semesters = parameters.filter { it.annotations.filterIsInstance<SemesterId>().isNotEmpty() }
        if (semesters.isNotEmpty()) {
            val semesterId = joinPoint.args[parameters.indexOf(semesters.first())]
            if (semesterId !is Long) {
                throw NotIdDataTypeException("semesterId", Long::class.java)
            }

            val result = checkBasedCourseSemester(semesterId, appUser, annotation)
            return finishProcessing(joinPoint, result)
        }

        val challenges = parameters.filter { it.annotations.filterIsInstance<ChallengeId>().isNotEmpty() }
        if (challenges.isNotEmpty()) {
            val challengeId = joinPoint.args[parameters.indexOf(challenges.first())]
            if (challengeId !is Long) {
                throw NotIdDataTypeException("challengeId", Long::class.java)
            }

            val result = checkBasedChallenge(challengeId, appUser, annotation)
            return finishProcessing(joinPoint, result)
        }

        val requirements = parameters.filter { it.annotations.filterIsInstance<RequirementId>().isNotEmpty() }
        if (requirements.isNotEmpty()) {
            val requirementId = joinPoint.args[parameters.indexOf(requirements.first())]
            if (requirementId !is Long) {
                throw NotIdDataTypeException("requirementId", Long::class.java)
            }

            val result = checkBasedRequirement(requirementId, appUser, annotation)
            return finishProcessing(joinPoint, result)
        }

        val solutions = parameters.filter { it.annotations.filterIsInstance<SolutionId>().isNotEmpty() }
        if (solutions.isNotEmpty()) {
            val solutionId = joinPoint.args[parameters.indexOf(solutions.first())]
            if (solutionId !is Long) {
                throw NotIdDataTypeException("solutionId", Long::class.java)
            }

            val result = checkBasedSolution(solutionId, appUser, annotation)
            return finishProcessing(joinPoint, result)
        }

        val reviews = parameters.filter { it.annotations.filterIsInstance<ReviewId>().isNotEmpty() }
        if (reviews.isNotEmpty()) {
            val reviewId = joinPoint.args[parameters.indexOf(parameters.first())]
            if (reviewId !is Long) {
                throw NotIdDataTypeException("reviewId", Long::class.java)
            }

            val result = checkBasedReview(reviewId, appUser, annotation)
            return finishProcessing(joinPoint, result)
        }

        val testResults = parameters.filter { it.annotations.filterIsInstance<TestResultId>().isNotEmpty() }
        if (testResults.isNotEmpty()) {
            val testReviewId = joinPoint.args[parameters.indexOf(parameters.first())]
            if (testReviewId !is Long) {
                throw NotIdDataTypeException("testResultId", Long::class.java)
            }

            val result = checkBasedTestResult(testReviewId, appUser, annotation)
            return finishProcessing(joinPoint, result)
        }

        return Mono.error(NoSemesterBasedIdInMethodArgumentsException(methodSignature, REQUIRED_ANNOTATIONS))
    }

    private fun checkBasedChallenge(
        challengeId: Long,
        appUser: AppUser,
        annotation: PreCourseSemesterAuthorize
    ): Mono<Boolean> {
        return courseSemesterRepository.findIdByChallengeId(challengeId)
            .switchIfEmpty(Mono.error(AuthorizeIdentificationNotFoundException(ChallengeId::class, challengeId)))
            .flatMap { semesterId -> authorizeService.hasCourseAccess(semesterId, appUser, annotation) }
            .flatMap { if (!it) Mono.error(AppUserCourseSemesterForbiddenException()) else Mono.just(it) }
    }

    private fun checkBasedCourseSemester(
        semesterId: Long,
        appUser: AppUser,
        annotation: PreCourseSemesterAuthorize
    ): Mono<Boolean> {
        return authorizeService.hasCourseAccess(semesterId, appUser, annotation)
            .flatMap { if (!it) Mono.error(AppUserCourseSemesterForbiddenException()) else Mono.just(it) }
    }

    private fun checkBasedRequirement(
        requirementId: Long,
        appUser: AppUser,
        annotation: PreCourseSemesterAuthorize
    ): Mono<Boolean> {
        return courseSemesterRepository.findIdByRequirementId(requirementId)
            .switchIfEmpty(Mono.error(AuthorizeIdentificationNotFoundException(RequirementId::class, requirementId)))
            .flatMap { semesterId -> authorizeService.hasCourseAccess(semesterId, appUser, annotation) }
            .flatMap { if (!it) Mono.error(AppUserCourseSemesterForbiddenException()) else Mono.just(it) }
    }

    private fun checkBasedSolution(
        solutionId: Long,
        appUser: AppUser,
        annotation: PreCourseSemesterAuthorize
    ): Mono<Boolean> {
        return courseSemesterRepository.findIdBySolutionId(solutionId)
            .switchIfEmpty(Mono.error(AuthorizeIdentificationNotFoundException(SolutionId::class, solutionId)))
            .flatMap { semesterId -> authorizeService.hasCourseAccess(semesterId, appUser, annotation) }
            .flatMap { if (!it) Mono.error(AppUserCourseSemesterForbiddenException()) else Mono.just(it) }
    }

    private fun checkBasedReview(
        reviewId: Long,
        appUser: AppUser,
        annotation: PreCourseSemesterAuthorize
    ): Mono<Boolean> {
        return courseSemesterRepository.findIdByReviewId(reviewId)
            .switchIfEmpty(Mono.error(AuthorizeIdentificationNotFoundException(ReviewId::class, reviewId)))
            .flatMap { semesterId -> authorizeService.hasCourseAccess(semesterId, appUser, annotation) }
            .flatMap { if (!it) Mono.error(AppUserCourseSemesterForbiddenException()) else Mono.just(it) }
    }

    private fun checkBasedTestResult(
        reviewId: Long,
        appUser: AppUser,
        annotation: PreCourseSemesterAuthorize
    ): Mono<Boolean> {
        return courseSemesterRepository.findIdByTestResultId(reviewId)
            .switchIfEmpty(Mono.error(AuthorizeIdentificationNotFoundException(ReviewId::class, reviewId)))
            .flatMap { semesterId -> authorizeService.hasCourseAccess(semesterId, appUser, annotation) }
            .flatMap { if (!it) Mono.error(AppUserCourseSemesterForbiddenException()) else Mono.just(it) }
    }

    private fun finishProcessing(
        joinPoint: ProceedingJoinPoint,
        result: Mono<*>,
    ): CorePublisher<out Any> {
        val methodSignature = joinPoint.signature as MethodSignature

        return when (methodSignature.returnType) {
            Mono::class.java -> result.then(joinPoint.proceed() as Mono<out Any>)
            Flux::class.java -> result.thenMany(joinPoint.proceed() as Flux<out Any>)
            else -> throw NotReactiveReturnTypeException(methodSignature)
        }
    }

    private fun isNotReactiveReturnType(returnType: Class<*>) =
        returnType != Mono::class.java && returnType != Flux::class.java

    private companion object {
        val REQUIRED_ANNOTATIONS = arrayOf(CourseId::class, SemesterId::class, ChallengeId::class)
    }
}