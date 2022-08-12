package com.projectronin.interop.queue

import com.projectronin.interop.common.hl7.EventType
import com.projectronin.interop.common.hl7.MessageType
import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.queue.model.ApiMessage
import com.projectronin.interop.queue.model.HL7Message
import com.projectronin.interop.queue.model.Message
import com.projectronin.interop.queue.model.QueueStatus

/**
 * Interface defining a Queue service.
 */
interface QueueService {
    /**
     * Adds the supplied [messages] to a Queue.
     */
    fun enqueueMessages(messages: List<Message>)

    /**
     * Reads [limit] [resourceType] [ApiMessage]s from the API Queue for the supplied [tenantMnemonic].
     */
    fun dequeueApiMessages(
        tenantMnemonic: String,
        resourceType: ResourceType,
        limit: Int
    ): List<ApiMessage>

    /**
     * Reads [limit] [hl7Type] (and optionally [hl7Event]) [HL7Message]s from the HL7 Queue for the supplied [tenantMnemonic].
     */
    fun dequeueHL7Messages(
        tenantMnemonic: String,
        hl7Type: MessageType,
        hl7Event: EventType?,
        limit: Int
    ): List<HL7Message>

    /**
     * Returns the current status of the queue.
     */
    fun getStatus(): QueueStatus
}
