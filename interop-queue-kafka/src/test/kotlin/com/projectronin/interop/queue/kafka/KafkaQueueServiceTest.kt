package com.projectronin.interop.queue.kafka

import com.projectronin.event.interop.resource.retrieve.v1.InteropResourceRetrieveV1
import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.kafka.client.createConsumer
import com.projectronin.interop.kafka.client.createProducer
import com.projectronin.interop.kafka.config.KafkaBootstrapConfig
import com.projectronin.interop.kafka.config.KafkaCloudConfig
import com.projectronin.interop.kafka.config.KafkaConfig
import com.projectronin.interop.kafka.config.KafkaPropertiesConfig
import com.projectronin.interop.kafka.config.KafkaPublishConfig
import com.projectronin.interop.kafka.config.KafkaRetrieveConfig
import com.projectronin.interop.kafka.config.KafkaSaslConfig
import com.projectronin.interop.kafka.config.KafkaSaslJaasConfig
import com.projectronin.interop.kafka.config.KafkaSecurityConfig
import com.projectronin.interop.queue.model.ApiMessage
import com.projectronin.kafka.RoninConsumer
import com.projectronin.kafka.RoninProducer
import com.projectronin.kafka.data.RoninEvent
import com.projectronin.kafka.data.RoninEventResult
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.invoke
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class KafkaQueueServiceTest {

    private lateinit var service: KafkaQueueService
    private lateinit var config: KafkaConfig

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
        service = KafkaQueueService(config, Spring().topics())
    }

    @Test
    fun `kafka messages enqueue`() {
        mockkStatic(::createProducer)
        val mockProducer = mockk<RoninProducer>()
        coEvery { mockProducer.send<InteropResourceRetrieveV1>(any(), any(), any()) } returns mockk()
        every { createProducer(any(), any()) } returns mockProducer
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
        unmockkAll()
    }

    @Test
    fun `can dequeue api messages`() {
        val mockEvent = mockk<RoninEvent<InteropResourceRetrieveV1>>()
        every { mockEvent.data } returns InteropResourceRetrieveV1("TENANT", "Patient", "Text")
        every { mockEvent.id } returns "messageID"
        mockkStatic(::createConsumer)
        val mockConsumer = mockk<RoninConsumer>()
        every { createConsumer(any(), any(), any()) } returns mockConsumer
        every { mockConsumer.process(handler = captureLambda()) } answers {
            lambda<(RoninEvent<*>) -> RoninEventResult>().invoke(mockEvent)
        }
        every { mockConsumer.stop() } just Runs

        val messages = service.dequeueApiMessages("TENANT", ResourceType.PATIENT, 1)
        assertEquals(1, messages.size)
        unmockkAll()
    }
}
