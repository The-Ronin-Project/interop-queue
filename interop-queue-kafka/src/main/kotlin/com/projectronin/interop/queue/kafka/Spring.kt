package com.projectronin.interop.queue.kafka

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Spring {

    @Bean
    fun kafkaConfig(): KafkaConfig {
        return KafkaConfig(
            cloud = KafkaCloudConfig(
                vendor = "local",
                region = "local"
            ),
            bootstrap = KafkaBootstrapConfig(
                servers = "kafka:19092"
            ),
            publish = KafkaPublishConfig(source = "interop-kafka-it"),
            retrieve = KafkaRetrieveConfig("interop-kafka-it"),
            properties = KafkaPropertiesConfig(
                security = KafkaSecurityConfig(protocol = "PLAINTEXT"),
                sasl = KafkaSaslConfig(
                    mechanism = "GSSAPI",
                    jaas = KafkaSaslJaasConfig(config = "")
                )
            )
        )
    }

    @Bean
    fun topics(): List<RetrieveTopic> {
        val patientRetrieveTopic = RetrieveTopic(
            systemName = "interop",
            topicName = "ronin.interop.patient.retrieve",
            dataSchema = "http://localhost/event/interop.patient",
            resourceType = "Patient"
        )
        return listOf(patientRetrieveTopic)
    }
}
