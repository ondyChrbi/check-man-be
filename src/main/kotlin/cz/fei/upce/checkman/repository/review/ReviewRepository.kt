package cz.fei.upce.checkman.repository.review

import cz.fei.upce.checkman.domain.review.Review
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ReviewRepository : ReactiveCrudRepository<Review, Long>