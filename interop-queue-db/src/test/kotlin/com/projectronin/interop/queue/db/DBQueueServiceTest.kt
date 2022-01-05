package com.projectronin.interop.queue.db

import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.queue.db.data.MessageDAO
import com.projectronin.interop.queue.model.Message
import com.projectronin.interop.queue.model.MessageType
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
        val message1 = Message(
            messageType = MessageType.API,
            resourceType = ResourceType.PATIENT,
            tenant = "TENANT",
            text = "Text"
        )
        val message2 = Message(
            messageType = MessageType.API,
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
    fun `can dequeue messages`() {
        val message1 = Message(
            messageType = MessageType.API,
            resourceType = ResourceType.PATIENT,
            tenant = "TENANT",
            text = "Text"
        )
        val message2 = Message(
            messageType = MessageType.API,
            resourceType = ResourceType.PATIENT,
            tenant = "TENANT",
            text = "Text"
        )

        every {
            messageDAO.readMessages(
                "TENANT",
                MessageType.API,
                ResourceType.PATIENT,
                2
            )
        } returns listOf(message1, message2)

        val messages = service.dequeueMessages("TENANT", MessageType.API, ResourceType.PATIENT, 2)
        assertEquals(listOf(message1, message2), messages)

        verify(exactly = 1) {
            messageDAO.readMessages("TENANT", MessageType.API, ResourceType.PATIENT, 2)
        }
    }
}
