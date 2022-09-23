package com.projectronin.interop.queue.monitor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Main Spring Boot application for the Interop proxy server.
 */
@ComponentScan(
    "com.projectronin.interop.queue.monitor",
    "com.projectronin.interop.queue.db"
)
@SpringBootApplication
@EnableScheduling
class InteropQueueMonitor

fun main(args: Array<String>) {
    runApplication<InteropQueueMonitor>(*args)
}
