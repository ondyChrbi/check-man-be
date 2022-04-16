package cz.fei.upce.checkman.repository.review

import cz.fei.upce.checkman.domain.review.RequirementReview
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RequirementReviewRepository : ReactiveCrudRepository<RequirementReview, Long>