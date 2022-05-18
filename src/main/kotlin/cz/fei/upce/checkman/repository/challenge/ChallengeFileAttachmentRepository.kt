package cz.fei.upce.checkman.repository.challenge

import cz.fei.upce.checkman.domain.challenge.ChallengeFileAttachment
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ChallengeFileAttachmentRepository : ReactiveCrudRepository<ChallengeFileAttachment, Long> {
    fun existsByChallengeIdEqualsAndFileAttachmentIdEquals(challengeId: Long, attachmentId: Long) : Mono<Boolean>
}