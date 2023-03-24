package com.projectronin.interop.queue.kafka

import com.projectronin.event.interop.resource.retrieve.v1.InteropResourceRetrieveV1
import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.kafka.client.KafkaClient
import com.projectronin.interop.kafka.spring.KafkaBootstrapConfig
import com.projectronin.interop.kafka.spring.KafkaCloudConfig
import com.projectronin.interop.kafka.spring.KafkaConfig
import com.projectronin.interop.kafka.spring.KafkaPropertiesConfig
import com.projectronin.interop.kafka.spring.KafkaPublishConfig
import com.projectronin.interop.kafka.spring.KafkaRetrieveConfig
import com.projectronin.interop.kafka.spring.KafkaSaslConfig
import com.projectronin.interop.kafka.spring.KafkaSaslJaasConfig
import com.projectronin.interop.kafka.spring.KafkaSecurityConfig
import com.projectronin.interop.queue.kafka.spring.KafkaQueueSpringConfig
import com.projectronin.interop.queue.model.ApiMessage
import com.projectronin.kafka.data.RoninEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class KafkaQueueServiceTest {

    private lateinit var service: KafkaQueueService
    private lateinit var config: KafkaConfig
    private lateinit var client: KafkaClient

    @BeforeEach
    fun setup() {
        val cloudConfig = KafkaCloudConfig(
            vendor = "local",
            region = "local"
        )

        config = KafkaConfig(
            cloud = cloudConfig,
            bootstrap = KafkaBootstrapConfig(servers = "localhost:9092"),
            publish = KafkaPublishConfig(source = "interop-kafka-it"),
            properties = KafkaPropertiesConfig(
                security = KafkaSecurityConfig(protocol = "PLAINTEXT"),
                sasl = KafkaSaslConfig(
                    mechanism = "GSSAPI",
                    jaas = KafkaSaslJaasConfig(config = "")
                )
            ),
            retrieve = KafkaRetrieveConfig("groupID")
        )
        client = mockk()
        service = KafkaQueueService(client, KafkaQueueSpringConfig(config).queueTopics())
    }

    @Test
    fun `kafka messages enqueue`() {
        every { client.publishEvents<InteropResourceRetrieveV1>(any(), any()) } returns mockk()
        val message1 = ApiMessage(
            resourceType = ResourceType.PRACTITIONER,
            tenant = "TENANT",
            text = "Text"
        )
        val message2 = ApiMessage(
            resourceType = ResourceType.APPOINTMENT,
            tenant = "TENANT",
            text = "Text"
        )
        val message3 = ApiMessage(
            resourceType = ResourceType.PATIENT,
            tenant = "TENANT",
            text = "Text"
        )

        assertDoesNotThrow { service.enqueueMessages(listOf(message1, message2, message3)) }
    }

    @Test
    fun `can dequeue api messages`() {
        val mockEvent = mockk<RoninEvent<InteropResourceRetrieveV1>>()
        every { mockEvent.data } returns InteropResourceRetrieveV1("TENANT", "Patient", "Text")
        every { mockEvent.id } returns "messageID"

        every { client.retrieveEvents(any(), any(), any(), any()) } returns listOf(mockEvent)

        val messages = service.dequeueApiMessages("TENANT", ResourceType.PATIENT, 1)
        assertEquals(1, messages.size)
        unmockkAll()
    }
}
