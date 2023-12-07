package com.projectronin.interop.queue.model

import com.projectronin.interop.common.hl7.EventType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import com.projectronin.interop.common.hl7.MessageType as HL7MessageType

class HL7MessageTest {
    @Test
    fun `check defaults`() {
        val message =
            HL7Message(
                tenant = "tenant",
                text = "text",
                hl7Type = HL7MessageType.MDM,
                hl7Event = EventType.MDMT02,
            )
        assertNull(message.id)
        assertEquals(MessageType.HL7, message.messageType)
        assertEquals(HL7MessageType.MDM, message.hl7Type)
        assertEquals(EventType.MDMT02, message.hl7Event)
        assertEquals("tenant", message.tenant)
        assertEquals("text", message.text)
    }

    @Test
    fun `check getters`() {
        val message =
            HL7Message(
                id = "id",
                tenant = "tenant",
                text = "text",
                hl7Type = HL7MessageType.MDM,
                hl7Event = EventType.MDMT02,
            )
        assertEquals("id", message.id)
        assertEquals(MessageType.HL7, message.messageType)
        assertEquals(HL7MessageType.MDM, message.hl7Type)
        assertEquals(EventType.MDMT02, message.hl7Event)
        assertEquals("tenant", message.tenant)
        assertEquals("text", message.text)
    }
}
