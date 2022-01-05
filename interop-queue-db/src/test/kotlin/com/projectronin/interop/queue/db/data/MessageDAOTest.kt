package com.projectronin.interop.queue.db.data

import com.github.database.rider.core.api.connection.ConnectionHolder
import com.github.database.rider.core.api.dataset.DataSet
import com.github.database.rider.core.api.dataset.ExpectedDataSet
import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.common.test.database.dbrider.DBRiderConnection
import com.projectronin.interop.common.test.database.ktorm.KtormHelper
import com.projectronin.interop.common.test.database.liquibase.LiquibaseTest
import com.projectronin.interop.queue.model.Message
import com.projectronin.interop.queue.model.MessageType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
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
        val messages = dao.readMessages("TENANT", MessageType.API, ResourceType.PATIENT, 5)
        assertEquals(listOf<Message>(), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/NoUnreadMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/NoUnreadMessages.yaml")
    fun `read API messages when no unread exist`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readMessages("TENANT", MessageType.API, ResourceType.PATIENT, 5)
        assertEquals(listOf<Message>(), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/OnlyAppointmentMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/OnlyAppointmentMessages.yaml")
    fun `read API messages when none exist for requested resource type`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readMessages("TENANT", MessageType.API, ResourceType.PATIENT, 5)
        assertEquals(listOf<Message>(), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/OnlyNewTenantMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/OnlyNewTenantMessages.yaml")
    fun `read API messages when none exist for requested tenant`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readMessages("TENANT", MessageType.API, ResourceType.PATIENT, 5)
        assertEquals(listOf<Message>(), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/UnreadMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/AllMessagesRead.yaml")
    fun `read API messages when no limit is supplied`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readMessages("TENANT", MessageType.API, ResourceType.PATIENT)

        val message1 = Message("100", MessageType.API, ResourceType.PATIENT, "TENANT", """{"id":2,"value":"Saved"}""")
        val message2 = Message("101", MessageType.API, ResourceType.PATIENT, "TENANT", """{"id":3,"value":"New"}""")
        val message3 = Message("102", MessageType.API, ResourceType.PATIENT, "TENANT", """{"id":4,"value":"New"}""")
        assertEquals(listOf(message1, message2, message3), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/UnreadMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/OldestMessagesRead.yaml")
    fun `read API messages when limit is less than available`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readMessages("TENANT", MessageType.API, ResourceType.PATIENT, 2)

        val message1 = Message("100", MessageType.API, ResourceType.PATIENT, "TENANT", """{"id":2,"value":"Saved"}""")
        val message2 = Message("101", MessageType.API, ResourceType.PATIENT, "TENANT", """{"id":3,"value":"New"}""")
        assertEquals(listOf(message1, message2), messages)
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/UnreadMessages.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/read/AllMessagesRead.yaml")
    fun `read API messages when limit is greater than available`() {
        val dao = MessageDAO(KtormHelper.database())
        val messages = dao.readMessages("TENANT", MessageType.API, ResourceType.PATIENT, 5)

        val message1 = Message("100", MessageType.API, ResourceType.PATIENT, "TENANT", """{"id":2,"value":"Saved"}""")
        val message2 = Message("101", MessageType.API, ResourceType.PATIENT, "TENANT", """{"id":3,"value":"New"}""")
        val message3 = Message("102", MessageType.API, ResourceType.PATIENT, "TENANT", """{"id":4,"value":"New"}""")
        assertEquals(listOf(message1, message2, message3), messages)
    }

    @Test
    fun `reading HL7 message throws exception`() {
        val dao = MessageDAO(KtormHelper.database())
        assertThrows(NotImplementedError::class.java) {
            dao.readMessages("TENANT", MessageType.HL7, ResourceType.PATIENT)
        }
    }

    @Test
    @DataSet(value = ["/dbunit/message/InitialSetup.yaml"], cleanAfter = true)
    @ExpectedDataSet("/dbunit/message/api/insert/ExpectedSingleMessage.yaml")
    fun `insert single API message`() {
        val dao = MessageDAO(KtormHelper.database())

        val message = Message(
            messageType = MessageType.API,
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

        val message1 = Message(
            messageType = MessageType.API,
            resourceType = ResourceType.PATIENT,
            tenant = "TEST1",
            text = """{"id":2,"value":"Saved"}"""
        )
        val message2 = Message(
            messageType = MessageType.API,
            resourceType = ResourceType.APPOINTMENT,
            tenant = "TEST1",
            text = """{"id":3,"value":"Upcoming"}"""
        )
        dao.insertMessages(listOf(message1, message2))
    }

    @Test
    fun `inserting HL7 message throws exception`() {
        val dao = MessageDAO(KtormHelper.database())

        val message = Message(
            messageType = MessageType.HL7,
            resourceType = ResourceType.PATIENT,
            tenant = "TEST1",
            text = """HL7Message"""
        )
        assertThrows(NotImplementedError::class.java) {
            dao.insertMessages(listOf(message))
        }
    }
}
