package com.projectronin.interop.queue.kafka.spring

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
        val supportedResources = listOf(
            "Patient",
            "Practitioner",
            "Appointment",
            "Condition"
        )
        return supportedResources.map {
            generateTopics(it)
        }
    }

    fun generateTopics(resourceType: String): RetrieveTopic {
        val topicName = listOf(
            kafkaSpringConfig.cloud.vendor,
            kafkaSpringConfig.cloud.region,
            "interop-proxy",
            "${resourceType.lowercase()}-retrieve",
            "v1"
        ).joinToString(".")
        return RetrieveTopic(
            systemName = "interop-proxy",
            topicName = topicName,
            dataSchema = "https://github.com/projectronin/contract-event-interop-patient-retrieve/blob/main/v1/interop-resource-retrieve-v1.schema.json",
            resourceType = resourceType
        )
    }
}
