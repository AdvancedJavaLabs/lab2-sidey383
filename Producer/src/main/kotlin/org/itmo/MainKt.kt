package org.itmo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MainKt

fun main(args: Array<String>) {
    runApplication<MainKt>(*args)
}