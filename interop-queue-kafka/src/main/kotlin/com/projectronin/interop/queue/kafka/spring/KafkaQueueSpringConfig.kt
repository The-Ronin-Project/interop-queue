package com.projectronin.interop.queue.kafka.spring

import com.projectronin.event.interop.internal.v1.ResourceType
import com.projectronin.event.interop.internal.v1.eventName
import com.projectronin.interop.kafka.model.RetrieveTopic
import com.projectronin.interop.kafka.spring.KafkaConfig
import com.projectronin.interop.kafka.spring.KafkaSpringConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan("com.projectronin.interop.queue.kafka")
@Import(KafkaSpringConfig::class)
class KafkaQueueSpringConfig(private val kafkaSpringConfig: KafkaConfig) {
    @Bean
    fun queueTopics(): List<RetrieveTopic> {
        val supportedResources =
            listOf(
                ResourceType.Patient,
                ResourceType.Practitioner,
                ResourceType.Appointment,
                ResourceType.Condition,
            )
        return supportedResources.map {
            generateTopics(it)
        }
    }

    fun generateTopics(resourceType: ResourceType): RetrieveTopic {
        val topicName =
            listOf(
                kafkaSpringConfig.cloud.vendor,
                kafkaSpringConfig.cloud.region,
                "interop-proxy",
                "${resourceType.eventName()}-retrieve",
                "v1",
            ).joinToString(".")
        @Suppress("ktlint:standard:max-line-length")
        return RetrieveTopic(
            systemName = "interop-proxy",
            topicName = topicName,
            dataSchema = "https://github.com/projectronin/contract-event-interop-patient-retrieve/blob/main/v1/interop-resource-retrieve-v1.schema.json",
            resourceType = resourceType,
        )
    }
}
