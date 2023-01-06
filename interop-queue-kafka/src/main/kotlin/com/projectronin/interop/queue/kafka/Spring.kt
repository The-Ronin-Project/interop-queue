package com.projectronin.interop.queue.kafka

import com.projectronin.interop.kafka.model.RetrieveTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Spring {

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
