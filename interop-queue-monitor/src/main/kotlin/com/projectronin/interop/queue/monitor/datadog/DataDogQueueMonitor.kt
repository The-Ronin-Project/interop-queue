package com.projectronin.interop.queue.monitor.datadog

import com.projectronin.interop.queue.QueueService
import com.timgroup.statsd.StatsDClient
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Class to monitor the queue and report results to DD.
 * [depthThreshold] is queue depth beneath which we won't bother sending the results to DataDog.
 */
@Component
class DataDogQueueMonitor(
    private val queueService: QueueService,
    private val statsd: StatsDClient,
    @Value("\${queue.monitor.threshold:1}") val depthThreshold: Int = 1
) {
    private val logger = KotlinLogging.logger { }

    /**
     * Function to monitor the queue status and report metrics to DataDog.  For each of the values in the returned
     * [QueueStatus] it reports a metric for each tenant and a value for all tenants.
     * "queue.monitor.rate" is the number of milliseconds to wait from the start of one status check to the next, read
     * from the application.properties file.
     */
    @Scheduled(fixedRateString = "\${queue.monitor.rate:10000}")
    fun monitorQueue() {
        logger.info { "Getting queue status" }
        val status = queueService.getStatus()

        logger.info { "Sending status to DataDog" }
        status.apiDepth
            .filter { it.value >= depthThreshold }
            .forEach { statsd.recordGaugeValue("${it.key}.api.depth.gauge", it.value.toLong()) }
        status.apiDepth.values.sum().takeIf { it >= depthThreshold }?.let {
            statsd.recordGaugeValue("api.depth.gauge", it.toLong())
        }

        status.hl7Depth
            .filter { it.value >= depthThreshold }
            .forEach { statsd.recordGaugeValue("${it.key}.hl7.depth.gauge", it.value.toLong()) }
        status.hl7Depth.values.sum().takeIf { it >= depthThreshold }?.let {
            statsd.recordGaugeValue("hl7.depth.gauge", it.toLong())
        }

        status.apiAge.forEach { statsd.recordGaugeValue("${it.key}.api.age.gauge", it.value.toLong()) }
        status.apiAge.values.maxOrNull()?.let {
            statsd.recordGaugeValue("api.age.gauge", it.toLong())
        }

        status.hl7Age.forEach { statsd.recordGaugeValue("${it.key}.hl7.age.gauge", it.value.toLong()) }
        status.hl7Age.values.maxOrNull()?.let {
            statsd.recordGaugeValue("hl7.age.gauge", it.toLong())
        }
    }
}
