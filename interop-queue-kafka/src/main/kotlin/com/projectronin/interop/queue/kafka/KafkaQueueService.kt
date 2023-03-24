package com.projectronin.interop.queue.kafka

import com.projectronin.event.interop.resource.retrieve.v1.InteropResourceRetrieveV1
import com.projectronin.interop.common.hl7.EventType
import com.projectronin.interop.common.hl7.MessageType
import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.kafka.client.KafkaClient
import com.projectronin.interop.kafka.model.KafkaAction
import com.projectronin.interop.kafka.model.KafkaEvent
import com.projectronin.interop.kafka.model.RetrieveTopic
import com.projectronin.interop.queue.QueueService
import com.projectronin.interop.queue.model.ApiMessage
import com.projectronin.interop.queue.model.HL7Message
import com.projectronin.interop.queue.model.Message
import com.projectronin.interop.queue.model.QueueStatus
import com.projectronin.kafka.data.RoninEvent
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.reflect.KClass

/**
 * Kafka-based implementation of [QueueService]
 */
@Service
class KafkaQueueService(
    val kafkaClient: KafkaClient,
    retrieveTopics: List<RetrieveTopic>
) : QueueService {
    private val retrieveTopicsByResourceType = retrieveTopics.groupBy { it.resourceType.lowercase() }
    private val typeMap: Map<String, KClass<*>> = mapOf(
        "ronin.interop-proxy.resource.retrieve" to InteropResourceRetrieveV1::class
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
            val topic = retrieveTopicsByResourceType[type]?.singleOrNull()
            if (topic == null) {
                KotlinLogging.logger { }.info { "Resource type '$type' is not supported for Kafka event queueing." }
                return@forEach
            }

            val events = list.map {
                val data = InteropResourceRetrieveV1(
                    resourceType = type,
                    resourceJson = it.text,
                    tenantId = it.tenant
                )
                KafkaEvent(
                    domain = topic.systemName,
                    resource = "resource",
                    action = KafkaAction.RETRIEVE,
                    resourceId = it.id ?: UUID.randomUUID().toString(),
                    data = data
                )
            }
            kafkaClient.publishEvents(topic, events)
        }
    }

    override fun dequeueApiMessages(
        tenantMnemonic: String,
        resourceType: ResourceType,
        limit: Int // used to differentiate group IDs (can make this less hacky when we get rid of DB queue)
    ): List<ApiMessage> {
        val topic = retrieveTopicsByResourceType[resourceType.name.lowercase()]?.singleOrNull() ?: return emptyList()
        return kafkaClient.retrieveEvents(topic, typeMap, "interop-mirth-queue-$limit").map { it.toAPIMessage(resourceType) }
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
