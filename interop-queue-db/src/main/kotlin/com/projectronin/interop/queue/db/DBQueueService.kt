package com.projectronin.interop.queue.db

import com.projectronin.interop.common.hl7.EventType
import com.projectronin.interop.common.hl7.MessageType
import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.queue.QueueService
import com.projectronin.interop.queue.db.data.MessageDAO
import com.projectronin.interop.queue.kafka.KafkaQueueService
import com.projectronin.interop.queue.model.ApiMessage
import com.projectronin.interop.queue.model.HL7Message
import com.projectronin.interop.queue.model.Message
import com.projectronin.interop.queue.model.QueueStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

/**
 * Database-based implementation of [QueueService]
 */
@Primary
@Service
class DBQueueService(
    private val messageDAO: MessageDAO,
    private val kafkaQueue: KafkaQueueService,
    @Value("\${queue.kafka.enabled:false}") private val useKafka: Boolean = false
) : QueueService {
    override fun enqueueMessages(messages: List<Message>) {
        // call kafka queue for patients only
        if (useKafka) {
            val patientMessages =
                messages.filterIsInstance<ApiMessage>().filter { it.resourceType == ResourceType.PATIENT }
            if (patientMessages.isNotEmpty()) kafkaQueue.enqueueMessages(patientMessages)
        }
        // still send all events to DB queue for now.
        return messageDAO.insertMessages(messages)
    }

    override fun dequeueApiMessages(
        tenantMnemonic: String,
        resourceType: ResourceType,
        limit: Int
    ): List<ApiMessage> {
        return messageDAO.readApiMessages(tenantMnemonic, resourceType, limit)
    }

    override fun dequeueHL7Messages(
        tenantMnemonic: String,
        hl7Type: MessageType,
        hl7Event: EventType?,
        limit: Int
    ): List<HL7Message> = messageDAO.readHL7Messages(tenantMnemonic, hl7Type, hl7Event, limit)

    override fun getStatus(): QueueStatus = messageDAO.getStatus()
}
