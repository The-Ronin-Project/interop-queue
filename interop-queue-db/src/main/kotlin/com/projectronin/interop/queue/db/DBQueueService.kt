package com.projectronin.interop.queue.db

import com.projectronin.interop.common.hl7.EventType
import com.projectronin.interop.common.hl7.MessageType
import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.queue.QueueService
import com.projectronin.interop.queue.db.data.MessageDAO
import com.projectronin.interop.queue.model.ApiMessage
import com.projectronin.interop.queue.model.HL7Message
import com.projectronin.interop.queue.model.Message
import com.projectronin.interop.queue.model.QueueStatus
import org.springframework.stereotype.Service

/**
 * Database-based implementation of [QueueService]
 */
@Service
class DBQueueService(private val messageDAO: MessageDAO) : QueueService {
    override fun enqueueMessages(messages: List<Message>) = messageDAO.insertMessages(messages)

    override fun dequeueApiMessages(
        tenantMnemonic: String,
        resourceType: ResourceType,
        limit: Int
    ): List<ApiMessage> = messageDAO.readApiMessages(tenantMnemonic, resourceType, limit)

    override fun dequeueHL7Messages(
        tenantMnemonic: String,
        hl7Type: MessageType,
        hl7Event: EventType?,
        limit: Int
    ): List<HL7Message> = messageDAO.readHL7Messages(tenantMnemonic, hl7Type, hl7Event, limit)

    override fun getStatus(): QueueStatus = messageDAO.getStatus()
}
