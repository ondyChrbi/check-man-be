package cz.fei.upce.checkman

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CheckManApplication

fun main(args: Array<String>) {
	runApplication<CheckManApplication>(*args)
}
