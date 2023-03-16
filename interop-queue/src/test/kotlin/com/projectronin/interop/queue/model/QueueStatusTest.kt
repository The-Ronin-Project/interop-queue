package com.projectronin.interop.queue.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class QueueStatusTest {
    @Test
    fun `check getters`() {
        val tenant = "tenant"
        val depth = 2
        val age = 10

        val status = QueueStatus(
            apiDepth = mapOf(tenant to depth),
            apiAge = mapOf(tenant to age),
            hl7Depth = mapOf(tenant to depth),
            hl7Age = mapOf(tenant to age)
        )

        assertEquals(depth, status.apiDepth[tenant])
        assertEquals(age, status.apiAge[tenant])
        assertEquals(depth, status.hl7Depth[tenant])
        assertEquals(age, status.hl7Age[tenant])
    }
}
