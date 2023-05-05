package com.projectronin.interop.queue.db.data

import com.github.database.rider.core.api.connection.ConnectionHolder
import com.github.database.rider.core.api.dataset.DataSet
import com.github.database.rider.core.api.dataset.ExpectedDataSet
import com.projectronin.interop.common.hl7.EventType
import com.projectronin.interop.common.hl7.MessageType
import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.common.test.database.dbrider.DBRiderConnection
import com.projectronin.interop.common.test.database.ktorm.KtormHelper
import com.projectronin.interop.common.test.database.liquibase.LiquibaseTest
import com.projectronin.interop.queue.model.ApiMessage
import com.projectronin.interop.queue.model.HL7Message
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

@LiquibaseTest(changeLog = "queue/db/changelog/queue.db.changelog-master.yaml")
class MessageDAOTest {
    @DBRiderConnection
    lateinit var connectionHolder: ConnectionHolder

    @Test
    @DataSet(value = ["/dbunit/message/api/read/NoMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/NoMessages.yaml")
    fun `read API messages when none exist`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readApiMessages("TENANT", ResourceType.PATIENT, 5)
        assertEquals(listOf<ApiMessage>(), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/NoUnreadMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/NoUnreadMessages.yaml")
    fun `read API messages when no unread exist`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readApiMessages("TENANT", ResourceType.PATIENT, 5)
        assertEquals(listOf<ApiMessage>(), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/OnlyAppointmentMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/OnlyAppointmentMessages.yaml")
    fun `read API messages when none exist for requested resource type`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readApiMessages("TENANT", ResourceType.PATIENT, 5)
        assertEquals(listOf<ApiMessage>(), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/OnlyNewTenantMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/OnlyNewTenantMessages.yaml")
    fun `read API messages when none exist for requested tenant`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readApiMessages("TENANT", ResourceType.PATIENT, 5)
        assertEquals(listOf<ApiMessage>(), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/UnreadMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/AllMessagesRead.yaml")
    fun `read API messages when no limit is supplied`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readApiMessages("TENANT", ResourceType.PATIENT)

        assertEquals("100", messages[0].id)
        assertEquals("TENANT", messages[0].tenant)
        assertEquals("""{"id":2,"value":"Saved"}""", messages[0].text)
        assertEquals(ResourceType.PATIENT, messages[0].resourceType)
        assertNotNull(messages[0].metadata)

        assertEquals("101", messages[1].id)
        assertEquals("TENANT", messages[1].tenant)
        assertEquals("""{"id":3,"value":"New"}""", messages[1].text)
        assertEquals(ResourceType.PATIENT, messages[1].resourceType)
        assertNotNull(messages[1].metadata)

        assertEquals("102", messages[2].id)
        assertEquals("TENANT", messages[2].tenant)
        assertEquals("""{"id":4,"value":"New"}""", messages[2].text)
        assertEquals(ResourceType.PATIENT, messages[2].resourceType)
        assertNotNull(messages[2].metadata)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/UnreadMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/OldestMessagesRead.yaml")
    fun `read API messages when limit is less than available`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readApiMessages("TENANT", ResourceType.PATIENT, 2)

        assertEquals("100", messages[0].id)
        assertEquals("TENANT", messages[0].tenant)
        assertEquals("""{"id":2,"value":"Saved"}""", messages[0].text)
        assertEquals(ResourceType.PATIENT, messages[0].resourceType)
        assertNotNull(messages[0].metadata)

        assertEquals("101", messages[1].id)
        assertEquals("TENANT", messages[1].tenant)
        assertEquals("""{"id":3,"value":"New"}""", messages[1].text)
        assertEquals(ResourceType.PATIENT, messages[1].resourceType)
        assertNotNull(messages[1].metadata)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/UnreadMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/AllMessagesRead.yaml")
    fun `read API messages when limit is greater than available`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readApiMessages("TENANT", ResourceType.PATIENT, 5)

        assertEquals("100", messages[0].id)
        assertEquals("TENANT", messages[0].tenant)
        assertEquals("""{"id":2,"value":"Saved"}""", messages[0].text)
        assertEquals(ResourceType.PATIENT, messages[0].resourceType)
        assertNotNull(messages[0].metadata)

        assertEquals("101", messages[1].id)
        assertEquals("TENANT", messages[1].tenant)
        assertEquals("""{"id":3,"value":"New"}""", messages[1].text)
        assertEquals(ResourceType.PATIENT, messages[1].resourceType)
        assertNotNull(messages[1].metadata)

        assertEquals("102", messages[2].id)
        assertEquals("TENANT", messages[2].tenant)
        assertEquals("""{"id":4,"value":"New"}""", messages[2].text)
        assertEquals(ResourceType.PATIENT, messages[2].resourceType)
        assertNotNull(messages[2].metadata)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/NoMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/NoMessages.yaml")
    fun `read HL7 messages when none exist`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readHL7Messages("TENANT", MessageType.MDM, null, 2)
        assertEquals(listOf<HL7Message>(), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/NoUnreadMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/NoUnreadMessages.yaml")
    fun `read HL7 messages when no unread exist`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readHL7Messages("TENANT", MessageType.MDM, null, 2)
        assertEquals(listOf<HL7Message>(), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/OnlyMDMT02Messages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/OnlyMDMT02Messages.yaml")
    fun `read HL7 messages when none exist for requested event type`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readHL7Messages("TENANT", MessageType.MDM, EventType.MDMT06, 2)
        assertEquals(listOf<HL7Message>(), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/OnlyNewTenantMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/OnlyNewTenantMessages.yaml")
    fun `read HL7 messages when none exist for requested tenant`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readHL7Messages("TENANT", MessageType.MDM, EventType.MDMT06, 2)
        assertEquals(listOf<HL7Message>(), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/UnreadHL7Messages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/OldestHl7MessagesRead.yaml")
    fun `read hl7 messages when limit is less than available`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readHL7Messages("TENANT", MessageType.MDM, null, 2)

        val message1 = HL7Message("100", "TENANT", """MSH|1231231""", MessageType.MDM, EventType.MDMT02)
        val message2 = HL7Message("101", "TENANT", """MSH|1231231""", MessageType.MDM, EventType.MDMT02)
        assertEquals(listOf(message1, message2), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/UnreadHL7Messages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/MDMT06MessagesRead.yaml")
    fun `read hl7 messages of only 1 type`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readHL7Messages("TENANT", MessageType.MDM, EventType.MDMT06, 5)

        val message1 = HL7Message("102", "TENANT", """MSH|1231231""", MessageType.MDM, EventType.MDMT06)
        assertEquals(listOf(message1), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/UnreadHL7Messages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/AllHl7MessagesRead.yaml")
    fun `read hl7 messages when limit is greater than available`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readHL7Messages("TENANT", MessageType.MDM)

        val message1 = HL7Message("100", "TENANT", """MSH|1231231""", MessageType.MDM, EventType.MDMT02)
        val message2 = HL7Message("101", "TENANT", """MSH|1231231""", MessageType.MDM, EventType.MDMT02)
        val message3 = HL7Message("102", "TENANT", """MSH|1231231""", MessageType.MDM, EventType.MDMT06)
        assertEquals(listOf(message1, message2, message3), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/InitialSetup.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/insert/ExpectedSingleMessage.yaml")
    fun `insert single API message`() {
        val dao = MessageDAO(KtormHelper.database())

        val message = ApiMessage(
            resourceType = ResourceType.PATIENT,
            tenant = "TEST1",
            text = """{"id":2,"value":"Saved"}""",
            metadata = mockk()
        )
        dao.insertMessages(listOf(message))
    }

    @Test
    @DataSet(value = ["/dbunit/message/InitialSetup.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/insert/ExpectedMultipleMessages.yaml")
    fun `insert multiple API messages`() {
        val dao = MessageDAO(KtormHelper.database())

        val message1 = ApiMessage(
            resourceType = ResourceType.PATIENT,
            tenant = "TEST1",
            text = """{"id":2,"value":"Saved"}""",
            metadata = mockk()
        )
        val message2 = ApiMessage(
            resourceType = ResourceType.APPOINTMENT,
            tenant = "TEST1",
            text = """{"id":3,"value":"Upcoming"}""",
            metadata = mockk()
        )
        dao.insertMessages(listOf(message1, message2))
    }

    @Test
    @DataSet(value = ["/dbunit/message/InitialSetup.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/insert/ExpectedMultipleMixedMessages.yaml")
    fun `insert multiple messages`() {
        val dao = MessageDAO(KtormHelper.database())

        val message1 = ApiMessage(
            resourceType = ResourceType.PATIENT,
            tenant = "TEST1",
            text = """{"id":2,"value":"Saved"}""",
            metadata = mockk()
        )
        val message2 = HL7Message(
            hl7Event = EventType.MDMT02,
            hl7Type = MessageType.MDM,
            tenant = "TEST1",
            text = """MSH|1231231"""
        )
        dao.insertMessages(listOf(message1, message2))
    }

    @Test
    @DataSet(value = ["/dbunit/message/InitialSetup.yaml"], cleanAfter = true)
    fun `can preserver hl7 messages formatting`() {
        val dao = MessageDAO(KtormHelper.database())
        val exampleMessage = this::class.java.getResource("/fakeHL7.txt")!!.readText()
        val message1 = HL7Message(
            tenant = "TEST1",
            text = exampleMessage,
            hl7Type = MessageType.MDM,
            hl7Event = EventType.MDMT02
        )
        dao.insertMessages(listOf(message1))
        val messages = dao.readHL7Messages("TEST1", MessageType.MDM)
        assertEquals(exampleMessage, messages.first().text)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/ApiAndHl7Messages.yaml"], cleanAfter = true)
    fun `can get queue status`() {
        // Mock the system clock so we can test queue age accurately
        val now = Instant.parse("2022-08-10T00:00:00.00Z")
        mockkStatic(Instant::class)
        every { Instant.now() } returns now

        val dao = MessageDAO(KtormHelper.database())
        val status = dao.getStatus()

        // From sample data
        val apiCreateInstant = Instant.parse("2021-11-01T11:18:00.00Z")
        val hl7CreateInstant1 = Instant.parse("2021-11-01T11:18:00.00Z")
        val hl7CreateInstant2 = Instant.parse("2022-08-08T11:24:00.00Z")

        // Api depth
        assertEquals(1, status.apiDepth.size)
        assertNull(status.apiDepth["FAKE"])
        assertNotNull(status.apiDepth["TENANT"])
        assertEquals(3, status.apiDepth["TENANT"])

        // Api age
        assertEquals(1, status.apiAge.size)
        assertNull(status.apiAge["FAKE"])
        assertNotNull(status.apiAge["TENANT"])
        assertEquals(Duration.between(apiCreateInstant, now).seconds.toInt(), status.apiAge["TENANT"])

        // HL7 depth
        assertEquals(2, status.hl7Depth.size)
        assertNull(status.hl7Depth["FAKE"])
        assertNotNull(status.hl7Depth["TENANT1"])
        assertEquals(1, status.hl7Depth["TENANT1"])
        assertNotNull(status.hl7Depth["TENANT2"])
        assertEquals(1, status.hl7Depth["TENANT2"])

        // HL7 age
        assertEquals(2, status.hl7Age.size)
        assertNull(status.hl7Age["FAKE"])
        assertNotNull(status.hl7Age["TENANT1"])
        assertEquals(Duration.between(hl7CreateInstant1, now).seconds.toInt(), status.hl7Age["TENANT1"])
        assertEquals(Duration.between(hl7CreateInstant2, now).seconds.toInt(), status.hl7Age["TENANT2"])

        unmockkStatic(Instant::class)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/NoMessages.yaml"], cleanAfter = true)
    fun `getStatus handles empty queues`() {
        val dao = MessageDAO(KtormHelper.database())
        val status = dao.getStatus()

        assertEquals(0, status.apiAge.size)
        assertEquals(0, status.apiDepth.size)
        assertEquals(0, status.hl7Age.size)
        assertEquals(0, status.hl7Depth.size)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/AllMessagesReadApiAndHl7.yaml"], cleanAfter = true)
    fun `getStatus handles all messages read`() {
        val dao = MessageDAO(KtormHelper.database())
        val status = dao.getStatus()

        assertEquals(0, status.apiAge.size)
        assertEquals(0, status.apiDepth.size)
        assertEquals(0, status.hl7Age.size)
        assertEquals(0, status.hl7Depth.size)
    }
}
