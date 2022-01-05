package com.projectronin.interop.queue

import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.queue.model.Message
import com.projectronin.interop.queue.model.MessageType

/**
 * Interface defining a Queue service.
 */
interface QueueService {
    /**
     * Adds the supplied [messages] to a Queue.
     */
    fun enqueueMessages(messages: List<Message>)

    /**
     * Reads [limit] [resourceType] [Message]s from the [messageType] Queue for the supplied [tenantMnemonic].
     */
    fun dequeueMessages(
        tenantMnemonic: String,
        messageType: MessageType,
        resourceType: ResourceType,
        limit: Int
    ): List<Message>
}
