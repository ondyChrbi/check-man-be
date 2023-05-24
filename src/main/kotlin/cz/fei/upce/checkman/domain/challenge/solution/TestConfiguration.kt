package cz.fei.upce.checkman.domain.challenge.solution

import cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestConfigurationQL
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("test_configuration")
data class TestConfiguration(
    @field:Id var id: Long? = null,
    @field:Column var templatePath: String? = null,
    @field:Column var dockerFilePath: String? = null,
    @field:Column var testModuleClass: String? = null,
    @field:Column var active: Boolean = false,
    @field:Column var creationDate: LocalDateTime = LocalDateTime.now(),
    @field:Column var updateDate: LocalDateTime? = null,
    @field:Column var challengeId: Long? = null,
) {
    fun toDto(): cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestConfigurationQL {
        return cz.fei.upce.checkman.dto.graphql.output.challenge.solution.TestConfigurationQL(
            id = this.id,
            templatePath = this.templatePath,
            dockerFilePath = this.dockerFilePath,
            testModuleClass = this.testModuleClass,
            active = this.active,
            creationDate = this.creationDate,
            updateDate = this.updateDate
        )
    }
}
