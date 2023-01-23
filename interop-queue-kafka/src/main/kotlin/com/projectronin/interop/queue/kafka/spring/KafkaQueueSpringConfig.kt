package com.projectronin.interop.queue.kafka.spring

import com.projectronin.interop.kafka.model.RetrieveTopic
import com.projectronin.interop.kafka.spring.KafkaSpringConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan("com.projectronin.interop.queue.kafka")
@Import(KafkaSpringConfig::class)
class KafkaQueueSpringConfig {
    @Bean
    fun topics(): List<RetrieveTopic> {
        val system = "interop"
        val dataSchema =
            "https://github.com/projectronin/contract-event-interop-patient-retrieve/blob/main/v1/interop-resource-retrieve-v1.schema.json"

        return listOf(
            RetrieveTopic(
                systemName = system,
                topicName = "azure.centralus.interop-proxy.patient-retrieve.v1",
                dataSchema = dataSchema,
                resourceType = "Patient"
            ),
            RetrieveTopic(
                systemName = system,
                topicName = "azure.centralus.interop-proxy.appointment-retrieve.v1",
                dataSchema = dataSchema,
                resourceType = "Appointment"
            ),
            RetrieveTopic(
                systemName = system,
                topicName = "azure.centralus.interop-proxy.practitioner-retrieve.v1",
                dataSchema = dataSchema,
                resourceType = "Practitioner"
            ),
            RetrieveTopic(
                systemName = system,
                topicName = "azure.centralus.interop-proxy.condition-retrieve.v1",
                dataSchema = dataSchema,
                resourceType = "Condition"
            )
        )
    }
}
