package com.projectronin.inteorp.queue.kafka

import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.kafka.model.RetrieveTopic
import com.projectronin.interop.queue.kafka.KafkaQueueService
import com.projectronin.interop.queue.model.ApiMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class KafkaQueueTest : BaseKafkaQueueServiceTest() {

    private val retrieveTopic = RetrieveTopic(
        systemName = "interop",
        topicName = "ronin.interop-proxy.patient.retrieve",
        dataSchema = "http://localhost/event/interop.patient",
        resourceType = "Patient"
    )
    private val kafkaQueueService = KafkaQueueService(kafkaClient, listOf(retrieveTopic))

    @Test
    @Disabled // INT-1605
    fun `can enqueue and dequeue`() {
        val message1 = ApiMessage(
            id = "12334",
            tenant = "ronin",
            resourceType = ResourceType.PATIENT,
            text = "someJSON"
        )
        val message2 = ApiMessage(
            id = "12334",
            tenant = "ronin",
            resourceType = ResourceType.PATIENT,
            text = "someJSON"
        )
        kafkaQueueService.enqueueMessages(listOf(message1, message2))
        val result = kafkaQueueService.dequeueApiMessages("", ResourceType.PATIENT, 2)
        assertEquals(2, result.size)
    }
}
