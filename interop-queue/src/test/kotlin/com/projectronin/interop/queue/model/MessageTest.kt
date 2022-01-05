package com.projectronin.interop.queue.model

import com.projectronin.interop.common.resource.ResourceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MessageTest {
    @Test
    fun `check defaults`() {
        val message = Message(
            messageType = MessageType.API,
            resourceType = ResourceType.PATIENT,
            tenant = "tenant",
            text = "text"
        )
        assertNull(message.id)
        assertEquals(MessageType.API, message.messageType)
        assertEquals(ResourceType.PATIENT, message.resourceType)
        assertEquals("tenant", message.tenant)
        assertEquals("text", message.text)
    }

    @Test
    fun `check getters`() {
        val message = Message(
            "id", MessageType.API, ResourceType.PATIENT, "tenant", "text"
        )
        assertEquals("id", message.id)
        assertEquals(MessageType.API, message.messageType)
        assertEquals(ResourceType.PATIENT, message.resourceType)
        assertEquals("tenant", message.tenant)
        assertEquals("text", message.text)
    }
}
