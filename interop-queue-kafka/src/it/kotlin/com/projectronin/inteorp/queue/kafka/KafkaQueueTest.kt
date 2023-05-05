package com.projectronin.inteorp.queue.kafka

import com.projectronin.event.interop.internal.v1.Metadata
import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.kafka.model.RetrieveTopic
import com.projectronin.interop.queue.kafka.KafkaQueueService
import com.projectronin.interop.queue.model.ApiMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class KafkaQueueTest : BaseKafkaQueueServiceTest() {
    private val retrieveTopic = RetrieveTopic(
        systemName = "interop-proxy",
        topicName = "ronin.interop-proxy.patient.retrieve",
        dataSchema = "http://localhost/event/interop.patient",
        resourceType = com.projectronin.event.interop.internal.v1.ResourceType.Patient
    )
    private val kafkaQueueService = KafkaQueueService(kafkaClient, listOf(retrieveTopic))

    @Test
    fun `can enqueue and dequeue`() {
        val metadata1 = Metadata(runId = UUID.randomUUID().toString(), runDateTime = OffsetDateTime.now(ZoneOffset.UTC))
        val message1 = ApiMessage(
            tenant = "ronin",
            resourceType = ResourceType.PATIENT,
            text = "someJSON",
            metadata = metadata1
        )
        val metadata2 = Metadata(runId = UUID.randomUUID().toString(), runDateTime = OffsetDateTime.now(ZoneOffset.UTC))
        val message2 = ApiMessage(
            tenant = "ronin",
            resourceType = ResourceType.PATIENT,
            text = "someJSON too",
            metadata = metadata2
        )
        kafkaQueueService.enqueueMessages(listOf(message1, message2))

        Thread.sleep(1_000)

        val result = kafkaQueueService.dequeueApiMessages("", ResourceType.PATIENT, 2)
        assertEquals(2, result.size)

        assertNotNull(result[0].id)
        assertEquals(ResourceType.PATIENT, result[0].resourceType)
        assertEquals("ronin", result[0].tenant)
        assertEquals("someJSON", result[0].text)
        assertEquals(metadata1, result[0].metadata)

        assertNotNull(result[1].id)
        assertEquals(ResourceType.PATIENT, result[1].resourceType)
        assertEquals("ronin", result[1].tenant)
        assertEquals("someJSON too", result[1].text)
        assertEquals(metadata2, result[1].metadata)
    }
}
