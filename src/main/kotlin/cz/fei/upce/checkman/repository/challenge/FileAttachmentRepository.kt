package cz.fei.upce.checkman.repository.challenge

import cz.fei.upce.checkman.domain.challenge.FileAttachment
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FileAttachmentRepository : ReactiveCrudRepository<FileAttachment, Long>