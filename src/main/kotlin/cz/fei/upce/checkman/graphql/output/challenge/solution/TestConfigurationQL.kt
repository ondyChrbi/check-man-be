package cz.fei.upce.checkman.graphql.output.challenge.solution

import java.time.LocalDateTime

data class TestConfigurationQL(
    var id: Long? = null,
    var templatePath: String? = null,
    var dockerFilePath: String? = null,
    var testModuleClass: String? = null,
    var active: Boolean = false,
    var creationDate: LocalDateTime = LocalDateTime.now(),
    var updateDate: LocalDateTime? = null,
)