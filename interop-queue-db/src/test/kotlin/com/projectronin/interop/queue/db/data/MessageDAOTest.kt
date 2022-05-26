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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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

        val message1 = ApiMessage("100", "TENANT", """{"id":2,"value":"Saved"}""", ResourceType.PATIENT)
        val message2 = ApiMessage("101", "TENANT", """{"id":3,"value":"New"}""", ResourceType.PATIENT)
        val message3 = ApiMessage("102", "TENANT", """{"id":4,"value":"New"}""", ResourceType.PATIENT)
        assertEquals(listOf(message1, message2, message3), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/UnreadMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/OldestMessagesRead.yaml")
    fun `read API messages when limit is less than available`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readApiMessages("TENANT", ResourceType.PATIENT, 2)

        val message1 = ApiMessage("100", "TENANT", """{"id":2,"value":"Saved"}""", ResourceType.PATIENT)
        val message2 = ApiMessage("101", "TENANT", """{"id":3,"value":"New"}""", ResourceType.PATIENT)
        assertEquals(listOf(message1, message2), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/UnreadMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/AllMessagesRead.yaml")
    fun `read API messages when limit is greater than available`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readApiMessages("TENANT", ResourceType.PATIENT, 5)

        val message1 = ApiMessage("100", "TENANT", """{"id":2,"value":"Saved"}""", ResourceType.PATIENT,)
        val message2 = ApiMessage("101", "TENANT", """{"id":3,"value":"New"}""", ResourceType.PATIENT)
        val message3 = ApiMessage("102", "TENANT", """{"id":4,"value":"New"}""", ResourceType.PATIENT)
        assertEquals(listOf(message1, message2, message3), messages)
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
            text = """{"id":2,"value":"Saved"}"""
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
            text = """{"id":2,"value":"Saved"}"""
        )
        val message2 = ApiMessage(
            resourceType = ResourceType.APPOINTMENT,
            tenant = "TEST1",
            text = """{"id":3,"value":"Upcoming"}"""
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
            text = """{"id":2,"value":"Saved"}"""
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
            hl7Event = EventType.MDMT02,
        )
        dao.insertMessages(listOf(message1))
        val messages = dao.readHL7Messages("TEST1", MessageType.MDM)
        assertEquals(exampleMessage, messages.first().text)
    }
}
