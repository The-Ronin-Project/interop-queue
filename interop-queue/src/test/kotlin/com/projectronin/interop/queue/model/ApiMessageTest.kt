package com.projectronin.interop.queue.model

import com.projectronin.event.interop.internal.v1.Metadata
import com.projectronin.interop.common.resource.ResourceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class ApiMessageTest {
    @Test
    fun `check defaults`() {
        val metadata = Metadata(runId = "run1", runDateTime = OffsetDateTime.now())
        val message = ApiMessage(
            tenant = "tenant",
            text = "text",
            resourceType = ResourceType.PATIENT,
            metadata = metadata
        )
        assertNull(message.id)
        assertEquals(MessageType.API, message.messageType)
        assertEquals(ResourceType.PATIENT, message.resourceType)
        assertEquals("tenant", message.tenant)
        assertEquals("text", message.text)
        assertEquals(metadata, message.metadata)
    }

    @Test
    fun `check getters`() {
        val metadata = Metadata(runId = "run1", runDateTime = OffsetDateTime.now())
        val message = ApiMessage(
            "id",
            "tenant",
            "text",
            ResourceType.PATIENT,
            metadata
        )
        assertEquals("id", message.id)
        assertEquals(MessageType.API, message.messageType)
        assertEquals(ResourceType.PATIENT, message.resourceType)
        assertEquals("tenant", message.tenant)
        assertEquals("text", message.text)
        assertEquals(metadata, message.metadata)
    }
}
