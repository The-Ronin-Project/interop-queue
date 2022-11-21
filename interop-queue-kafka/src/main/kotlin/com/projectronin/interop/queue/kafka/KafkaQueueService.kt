package com.projectronin.interop.queue.kafka

import com.projectronin.event.interop.patient.retrieve.v1.InteropResourceRetrieveV1
import com.projectronin.interop.common.hl7.EventType
import com.projectronin.interop.common.hl7.MessageType
import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.queue.QueueService
import com.projectronin.interop.queue.model.ApiMessage
import com.projectronin.interop.queue.model.HL7Message
import com.projectronin.interop.queue.model.Message
import com.projectronin.interop.queue.model.QueueStatus
import com.projectronin.kafka.RoninProducer
import com.projectronin.kafka.data.RoninEvent
import com.projectronin.kafka.data.RoninEventResult
import org.springframework.stereotype.Service
import java.util.Timer
import java.util.UUID
import kotlin.concurrent.schedule
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
    private val producersByTopicName: MutableMap<String, RoninProducer> = mutableMapOf()
    private val typeMap: Map<String, KClass<*>> = mapOf(
        "ronin.interop.resource.retrieve" to InteropResourceRetrieveV1::class,
    )

    fun RoninEvent<*>.toAPIMessage(type: ResourceType): ApiMessage {
        val resourceRetrieve = this.data as InteropResourceRetrieveV1
        return ApiMessage(
            id = this.id,
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
            val producer = producersByTopicName.computeIfAbsent(topic.topicName) { createProducer(topic, kafkaConfig) }
            list.forEach {
                val data = InteropResourceRetrieveV1(
                    resourceType = type,
                    resourceJson = it.text,
                    tenantId = it.tenant
                )
                val event = KafkaEvent(
                    domain = topic.systemName,
                    resource = "resource",
                    action = KafkaAction.RETRIEVE,
                    resourceId = it.id ?: UUID.randomUUID().toString(),
                    data = data
                )
                producer.send(event.type, event.subject, event.data)
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
        val consumer = createConsumer(topic, typeMap, kafkaConfig)
        Timer("poll").schedule(5000) {
            consumer.stop()
        }
        consumer.process {
            messageList.add(it.toAPIMessage(resourceType))
            // stop if 5 seconds have passed, or we have reached our message limit
            // note that the 'limit' might not be exact, since there could still be messages left to process
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