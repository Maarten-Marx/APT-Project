package me.maartenmarx.interactionservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InteractionServiceApplication

fun main(args: Array<String>) {
    runApplication<InteractionServiceApplication>(*args)
}
