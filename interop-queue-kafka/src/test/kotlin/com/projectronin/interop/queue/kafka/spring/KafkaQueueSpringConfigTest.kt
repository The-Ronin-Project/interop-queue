package com.projectronin.interop.queue.kafka.spring

import com.projectronin.interop.queue.kafka.KafkaQueueService
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [KafkaQueueSpringConfig::class])
@TestPropertySource("kafka-test.properties")
class KafkaQueueSpringConfigTest {
    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun `loads KafkaQueueService`() {
        val service = applicationContext.getBean<KafkaQueueService>()
        assertNotNull(service)
        assertInstanceOf(KafkaQueueService::class.java, service)
    }
}
