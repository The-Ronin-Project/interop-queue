package com.projectronin.interop.queue.monitor.datadog

import com.projectronin.interop.queue.QueueService
import com.projectronin.interop.queue.model.QueueStatus
import com.timgroup.statsd.NonBlockingStatsDClient
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test

class DataDogQueueMonitorTest {
    private var queueService = mockk<QueueService>()
    private var statsDClient = mockk<NonBlockingStatsDClient>()
    private var monitor = DataDogQueueMonitor(queueService, statsDClient)

    @Test
    fun `can handle one tenant at a time`() {
        every { queueService.getStatus() } returns QueueStatus(
            apiDepth = mapOf("tenant" to 1),
            hl7Depth = mapOf(),
            apiAge = mapOf("tenant" to 1),
            hl7Age = mapOf()
        )

        every { statsDClient.recordGaugeValue("tenant.api.depth.gauge", 1) } just runs
        every { statsDClient.recordGaugeValue("tenant.api.age.gauge", 1) } just runs
        every { statsDClient.recordGaugeValue("api.depth.gauge", 1) } just runs
        every { statsDClient.recordGaugeValue("api.age.gauge", 1) } just runs

        monitor.monitorQueue()

        verify(exactly = 1) { statsDClient.recordGaugeValue("tenant.api.depth.gauge", 1) }
        verify(exactly = 1) { statsDClient.recordGaugeValue("tenant.api.age.gauge", 1) }
        verify(exactly = 1) { statsDClient.recordGaugeValue("api.depth.gauge", 1) }
        verify(exactly = 1) { statsDClient.recordGaugeValue("api.age.gauge", 1) }
    }

    @Test
    fun `can handle multiple tenants in both queues`() {
        every { queueService.getStatus() } returns QueueStatus(
            apiDepth = mapOf(
                "tenant1" to 3,
                "tenant2" to 4
            ),
            hl7Depth = mapOf(
                "tenant1" to 5,
                "tenant2" to 6
            ),
            apiAge = mapOf(
                "tenant1" to 10,
                "tenant2" to 11
            ),
            hl7Age = mapOf(
                "tenant1" to 13,
                "tenant2" to 14
            )
        )

        every { statsDClient.recordGaugeValue("tenant1.api.depth.gauge", 3) } just runs
        every { statsDClient.recordGaugeValue("tenant2.api.depth.gauge", 4) } just runs
        every { statsDClient.recordGaugeValue("tenant1.hl7.depth.gauge", 5) } just runs
        every { statsDClient.recordGaugeValue("tenant2.hl7.depth.gauge", 6) } just runs
        every { statsDClient.recordGaugeValue("tenant1.api.age.gauge", 10) } just runs
        every { statsDClient.recordGaugeValue("tenant2.api.age.gauge", 11) } just runs
        every { statsDClient.recordGaugeValue("tenant1.hl7.age.gauge", 13) } just runs
        every { statsDClient.recordGaugeValue("tenant2.hl7.age.gauge", 14) } just runs

        every { statsDClient.recordGaugeValue("api.depth.gauge", 7) } just runs
        every { statsDClient.recordGaugeValue("api.age.gauge", 11) } just runs
        every { statsDClient.recordGaugeValue("hl7.depth.gauge", 11) } just runs
        every { statsDClient.recordGaugeValue("hl7.age.gauge", 14) } just runs

        monitor.monitorQueue()

        verify(exactly = 1) { statsDClient.recordGaugeValue("tenant1.api.depth.gauge", 3) }
        verify(exactly = 1) { statsDClient.recordGaugeValue("tenant2.api.depth.gauge", 4) }
        verify(exactly = 1) { statsDClient.recordGaugeValue("tenant1.hl7.depth.gauge", 5) }
        verify(exactly = 1) { statsDClient.recordGaugeValue("tenant2.hl7.depth.gauge", 6) }
        verify(exactly = 1) { statsDClient.recordGaugeValue("tenant1.api.age.gauge", 10) }
        verify(exactly = 1) { statsDClient.recordGaugeValue("tenant2.api.age.gauge", 11) }
        verify(exactly = 1) { statsDClient.recordGaugeValue("tenant1.hl7.age.gauge", 13) }
        verify(exactly = 1) { statsDClient.recordGaugeValue("tenant2.hl7.age.gauge", 14) }

        verify(exactly = 1) { statsDClient.recordGaugeValue("api.depth.gauge", 7) }
        verify(exactly = 1) { statsDClient.recordGaugeValue("api.age.gauge", 11) }
        verify(exactly = 1) { statsDClient.recordGaugeValue("hl7.depth.gauge", 11) }
        verify(exactly = 1) { statsDClient.recordGaugeValue("hl7.age.gauge", 14) }
    }

    @Test
    fun `values under the threshold are not sent`() {
        every { queueService.getStatus() } returns QueueStatus(
            apiDepth = mapOf("tenant" to 0),
            hl7Depth = mapOf("tenant" to 0),
            apiAge = mapOf(),
            hl7Age = mapOf()
        )

        every { statsDClient.recordGaugeValue("tenant.api.depth.gauge", 0) } just runs
        every { statsDClient.recordGaugeValue("tenant.hl7.depth.gauge", 0) } just runs

        every { statsDClient.recordGaugeValue("api.depth.gauge", 0) } just runs
        every { statsDClient.recordGaugeValue("hl7.depth.gauge", 0) } just runs

        monitor.monitorQueue()

        verify(exactly = 0) { statsDClient.recordGaugeValue("tenant.api.depth.gauge", 0) }
        verify(exactly = 0) { statsDClient.recordGaugeValue("tenant.hl7.depth.gauge", 0) }

        verify(exactly = 0) { statsDClient.recordGaugeValue("api.depth.gauge", 0) }
        verify(exactly = 0) { statsDClient.recordGaugeValue("hl7.depth.gauge", 0) }
    }
}
