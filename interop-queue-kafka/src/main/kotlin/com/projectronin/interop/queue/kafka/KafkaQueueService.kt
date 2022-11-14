package com.projectronin.interop.queue.kafka

import com.projectronin.event.interop.patient.retrieve.v1.InteropResourceRetrieveV1
import com.projectronin.interop.common.hl7.EventType
import com.projectronin.interop.common.hl7.MessageType
import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.kafka.client.createConsumer
import com.projectronin.interop.kafka.client.createProducer
import com.projectronin.interop.kafka.config.KafkaConfig
import com.projectronin.interop.kafka.model.KafkaAction
import com.projectronin.interop.kafka.model.KafkaEvent
import com.projectronin.interop.kafka.model.RetrieveTopic
import com.projectronin.interop.queue.QueueService
import com.projectronin.interop.queue.model.ApiMessage
import com.projectronin.interop.queue.model.HL7Message
import com.projectronin.interop.queue.model.Message
import com.projectronin.interop.queue.model.QueueStatus
import com.projectronin.kafka.data.RoninEvent
import com.projectronin.kafka.data.RoninEventResult
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.reflect.KClass

/**
 * Database-based implementation of [QueueService]
 */
@Service
class KafkaQueueService(
    val kafkaConfig: KafkaConfig,
    retrieveTopics: List<RetrieveTopic>
) : QueueService {
    private val retrieveTopicsByResourceType = retrieveTopics.groupBy { it.resourceType.lowercase() }
    private val typeMap: Map<String, KClass<*>> = mapOf(
        "ronin.interop.resource.retrieve" to InteropResourceRetrieveV1::class,
    )

    fun RoninEvent<*>.toAPIMessage(type: ResourceType): ApiMessage {
        val resourceRetrieve = this.data as InteropResourceRetrieveV1
        return ApiMessage(
            id = UUID.randomUUID().toString(),
            tenant = resourceRetrieve.tenantId,
            resourceType = type,
            text = resourceRetrieve.resourceJson
        )
    }

    override fun enqueueMessages(messages: List<Message>) {
        val apiMessages = messages.filterIsInstance<ApiMessage>()
        val apiMessagesByType = apiMessages.groupBy { it.resourceType.name.lowercase() }
        apiMessagesByType.forEach { (type, list) ->
            val topic = retrieveTopicsByResourceType[type]?.singleOrNull() ?: return@forEach
            val messagesByTenant = list.groupBy { it.tenant }
            messagesByTenant.forEach { (tenant, list) ->
                val producer =
                    createProducer(topicName = topic.getTopicName(kafkaConfig.cloud, tenant), topic, kafkaConfig)
                list.forEach {
                    val event = KafkaEvent(
                        domain = topic.systemName,
                        resource = type,
                        action = KafkaAction.RETRIEVE,
                        resourceId = it.id!!,
                        data = it.text
                    )
                    producer.send(event.type, event.subject, event.data)
                }
            }
        }
    }

    override fun dequeueApiMessages(
        tenantMnemonic: String,
        resourceType: ResourceType,
        limit: Int
    ): List<ApiMessage> {
        val messageList = mutableListOf<ApiMessage>()
        val topic = retrieveTopicsByResourceType[resourceType.name.lowercase()]?.singleOrNull() ?: return emptyList()
        val consumer = createConsumer(topic.getTopicName(kafkaConfig.cloud, tenantMnemonic), kafkaConfig, typeMap)
        consumer.process {
            messageList.add(it.toAPIMessage(resourceType))
            if (messageList.size == limit) consumer.stop()
            RoninEventResult.ACK
        }
        return messageList
    }

    override fun dequeueHL7Messages(
        tenantMnemonic: String,
        hl7Type: MessageType,
        hl7Event: EventType?,
        limit: Int
    ): List<HL7Message> {
        TODO()
    }

    override fun getStatus(): QueueStatus {
        TODO()
    }
}
