package com.projectronin.interop.queue.model

import com.projectronin.interop.common.resource.ResourceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ApiMessageTest {
    @Test
    fun `check defaults`() {
        val message = ApiMessage(
            tenant = "tenant",
            text = "text",
            resourceType = ResourceType.PATIENT
        )
        assertNull(message.id)
        assertEquals(MessageType.API, message.messageType)
        assertEquals(ResourceType.PATIENT, message.resourceType)
        assertEquals("tenant", message.tenant)
        assertEquals("text", message.text)
    }

    @Test
    fun `check getters`() {
        val message = ApiMessage(
            "id",
            "tenant",
            "text",
            ResourceType.PATIENT
        )
        assertEquals("id", message.id)
        assertEquals(MessageType.API, message.messageType)
        assertEquals(ResourceType.PATIENT, message.resourceType)
        assertEquals("tenant", message.tenant)
        assertEquals("text", message.text)
    }
}
