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
            dataSchema = "http://localhost/event/interop.retrieve",
            resourceType = "Patient"
        )
        return listOf(patientRetrieveTopic)
    }
}
