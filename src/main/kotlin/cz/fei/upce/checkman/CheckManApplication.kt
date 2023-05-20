package cz.fei.upce.checkman

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.domain.PageRequest
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing

@SpringBootApplication
@EnableR2dbcAuditing
class CheckManApplication {
	companion object {
		const val DEFAULT_PAGE = 0
		const val DEFAULT_PAGE_SIZE = Int.MAX_VALUE
		const val DEFAULT_SORT_FIELD = "id"
		val DEFAULT_PAGEABLE = PageRequest.of(DEFAULT_PAGE, DEFAULT_PAGE_SIZE)
	}
}

fun main(args: Array<String>) {
	runApplication<CheckManApplication>(*args)
}
