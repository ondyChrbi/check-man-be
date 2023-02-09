package cz.fei.upce.checkman

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@SpringBootApplication
@EnableR2dbcAuditing
class CheckManApplication {
	companion object {
		const val DEFAULT_OFFSET = 0
		const val DEFAULT_SIZE = 2
	}
}

fun main(args: Array<String>) {
	runApplication<CheckManApplication>(*args)
}
