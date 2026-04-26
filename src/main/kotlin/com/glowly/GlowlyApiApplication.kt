package com.glowly

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class GlowlyApiApplication

fun main(args: Array<String>) {
    runApplication<GlowlyApiApplication>(*args)
}