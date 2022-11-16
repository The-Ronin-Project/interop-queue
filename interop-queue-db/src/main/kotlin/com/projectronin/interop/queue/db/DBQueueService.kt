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
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

/**
 * Database-based implementation of [QueueService]
 */
@Primary
@Service
class DBQueueService(private val messageDAO: MessageDAO, private val kafkaQueue: KafkaQueueService) : QueueService {
    override fun enqueueMessages(messages: List<Message>) {
        val nonPatientMessages = mutableListOf<Message>()
        val patientMessages = mutableListOf<Message>()
        messages.forEach {
            if (it is ApiMessage && it.resourceType == ResourceType.PATIENT) patientMessages.add(it)
            else nonPatientMessages.add(it)
        }
        if (patientMessages.isNotEmpty()) kafkaQueue.enqueueMessages(patientMessages)
        return messageDAO.insertMessages(messages)
    }

    override fun dequeueApiMessages(
        tenantMnemonic: String,
        resourceType: ResourceType,
        limit: Int
    ): List<ApiMessage> {
        if (resourceType == ResourceType.PATIENT) {
            return kafkaQueue.dequeueApiMessages(tenantMnemonic, resourceType, limit)
        }
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
