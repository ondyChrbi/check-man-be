package cz.fei.upce.checkman.repository.challenge

import cz.fei.upce.checkman.domain.challenge.ChallengeFileAttachment
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface ChallengeFileAttachmentRepository : ReactiveCrudRepository<ChallengeFileAttachment, Long>