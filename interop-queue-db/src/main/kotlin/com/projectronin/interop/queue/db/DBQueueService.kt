package com.projectronin.interop.queue.db

import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.queue.QueueService
import com.projectronin.interop.queue.db.data.MessageDAO
import com.projectronin.interop.queue.model.Message
import com.projectronin.interop.queue.model.MessageType
import org.springframework.stereotype.Service

/**
 * Database-based implementation of [QueueService]
 */
@Service
class DBQueueService(private val messageDAO: MessageDAO) : QueueService {
    override fun enqueueMessages(messages: List<Message>) = messageDAO.insertMessages(messages)

    override fun dequeueMessages(
        tenantMnemonic: String,
        messageType: MessageType,
        resourceType: ResourceType,
        limit: Int
    ): List<Message> = messageDAO.readMessages(tenantMnemonic, messageType, resourceType, limit)
}
