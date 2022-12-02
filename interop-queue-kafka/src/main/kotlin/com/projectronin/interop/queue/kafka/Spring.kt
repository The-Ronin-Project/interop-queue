package com.projectronin.interop.queue.kafka

import com.projectronin.interop.kafka.model.RetrieveTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Spring {

    @Bean
    fun topics(): List<RetrieveTopic> {
        val patientRetrieveTopic = RetrieveTopic(
            systemName = "interop",
            topicName = "ronin.interop.patient.retrieve",
            dataSchema = "https://github.com/projectronin/contract-event-interop-patient-retrieve/blob/main/v1/interop-resource-retrieve-v1.schema.json",
            resourceType = "Patient"
        )
        return listOf(patientRetrieveTopic)
    }
}
