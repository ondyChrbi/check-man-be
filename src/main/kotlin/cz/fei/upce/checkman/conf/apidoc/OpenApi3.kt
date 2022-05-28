package cz.fei.upce.checkman.conf.apidoc

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.servers.Server

@SecurityScheme(
    name = "bearerAuth", type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT", scheme = "bearer"
)
@OpenAPIDefinition(
    info = Info(
        title = "Check man REST API documentation",
        description = "Official REST documentation",
        contact = Contact(name = "Ondřej Chrbolka", email = "ondrej.chrbolka@upce.cz"),
        version = "0.0.1"
    ), servers = [Server(url = "https://localhost:9001", description = "Local docker services")]
)
class OpenApi3