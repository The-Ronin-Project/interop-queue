package com.projectronin.interop.queue.db

import com.projectronin.interop.common.hl7.EventType
import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.queue.db.data.MessageDAO
import com.projectronin.interop.queue.model.ApiMessage
import com.projectronin.interop.queue.model.HL7Message
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.projectronin.interop.common.hl7.MessageType as HL7MessageType
class DBQueueServiceTest {
    private lateinit var messageDAO: MessageDAO
    private lateinit var service: DBQueueService

    @BeforeEach
    fun setup() {
        messageDAO = mockk()

        service = DBQueueService(messageDAO)
    }

    @Test
    fun `can enqueue messages`() {
        val message1 = ApiMessage(
            resourceType = ResourceType.PATIENT,
            tenant = "TENANT",
            text = "Text"
        )
        val message2 = ApiMessage(
            resourceType = ResourceType.APPOINTMENT,
            tenant = "TENANT",
            text = "Text"
        )
        every { messageDAO.insertMessages(listOf(message1, message2)) } just Runs

        service.enqueueMessages(listOf(message1, message2))

        verify(exactly = 1) {
            messageDAO.insertMessages(listOf(message1, message2))
        }
    }

    @Test
    fun `can dequeue api messages`() {
        val message1 = ApiMessage(
            resourceType = ResourceType.PATIENT,
            tenant = "TENANT",
            text = "Text"
        )
        val message2 = ApiMessage(
            resourceType = ResourceType.PATIENT,
            tenant = "TENANT",
            text = "Text"
        )

        every {
            messageDAO.readApiMessages(
                "TENANT",
                ResourceType.PATIENT,
                2
            )
        } returns listOf(message1, message2)

        val messages = service.dequeueApiMessages("TENANT", ResourceType.PATIENT, 2)
        assertEquals(listOf(message1, message2), messages)

        verify(exactly = 1) {
            messageDAO.readApiMessages("TENANT", ResourceType.PATIENT, 2)
        }
    }

    @Test
    fun `can dequeue hl7 messages`() {
        val message1 = HL7Message(
            tenant = "TENANT",
            text = "Text",
            hl7Type = HL7MessageType.MDM,
            hl7Event = EventType.MDMT02,
        )
        val message2 = HL7Message(
            tenant = "TENANT",
            text = "Text",
            hl7Type = HL7MessageType.MDM,
            hl7Event = EventType.MDMT02,
        )

        every {
            messageDAO.readHL7Messages(
                "TENANT",
                HL7MessageType.MDM,
                EventType.MDMT02,
                2
            )
        } returns listOf(message1, message2)

        val messages = service.dequeueHL7Messages("TENANT", HL7MessageType.MDM, EventType.MDMT02, 2)
        assertEquals(listOf(message1, message2), messages)

        verify(exactly = 1) {
            messageDAO.readHL7Messages("TENANT", HL7MessageType.MDM, EventType.MDMT02, 2)
        }
    }
}
