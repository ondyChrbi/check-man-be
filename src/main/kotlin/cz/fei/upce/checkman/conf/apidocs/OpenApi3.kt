package cz.fei.upce.checkman.conf.apidocs

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server

@OpenAPIDefinition(
    info = Info(
        title = "Check man REST API documentation",
        description = "Official REST documentation",
        contact = Contact(name = "Ondřej Chrbolka", email = "ondrej.chrbolka@upce.cz"),
        version = "0.0.1"
    ), servers = [Server(url = "http://localhost:9001", description = "Local docker services")]
)
class OpenApi3