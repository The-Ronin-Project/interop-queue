package com.projectronin.interop.queue.kafka

import com.projectronin.kafka.RoninConsumer
import com.projectronin.kafka.RoninProducer
import com.projectronin.kafka.config.RoninConsumerKafkaProperties
import com.projectronin.kafka.config.RoninProducerKafkaProperties
import kotlin.reflect.KClass

data class KafkaConfig(
    val cloud: KafkaCloudConfig,
    val bootstrap: KafkaBootstrapConfig,
    val publish: KafkaPublishConfig,
    val retrieve: KafkaRetrieveConfig,
    val properties: KafkaPropertiesConfig = KafkaPropertiesConfig()
)

data class KafkaCloudConfig(
    val vendor: String,
    val region: String
)

data class KafkaBootstrapConfig(
    val servers: String
)

data class KafkaPublishConfig(
    val source: String
)

data class KafkaRetrieveConfig(
    val groupId: String
)

data class KafkaPropertiesConfig(
    val security: KafkaSecurityConfig = KafkaSecurityConfig(),
    val sasl: KafkaSaslConfig = KafkaSaslConfig()
)

data class KafkaSecurityConfig(
    val protocol: String? = null
)

data class KafkaSaslConfig(
    val mechanism: String? = null,
    val username: String? = null,
    val password: String? = null,
    val jaas: KafkaSaslJaasConfig = KafkaSaslJaasConfig()
)

data class KafkaSaslJaasConfig(
    val config: String? = null
)

data class RetrieveTopic(
    override val systemName: String,
    override val topicName: String,
    override val dataSchema: String,
    val resourceType: String,
) : KafkaTopic

interface KafkaTopic {
    val systemName: String
    val topicName: String
    val dataSchema: String
}

/**
 * Creates a [RoninProducer] capable of publishing to the Kafka [topic] represented by [topicName].
 */
fun createProducer(
    topic: KafkaTopic,
    kafkaConfig: KafkaConfig
): RoninProducer {
    val kafkaProperties = kafkaConfig.properties
    val producerProperties = RoninProducerKafkaProperties(
        "bootstrap.servers" to kafkaConfig.bootstrap.servers,
        "security.protocol" to kafkaProperties.security.protocol,
        "sasl.mechanism" to kafkaProperties.sasl.mechanism,
        "sasl.jaas.config" to kafkaProperties.sasl.jaas.config
    )
    return RoninProducer(
        topic = topic.topicName,
        source = kafkaConfig.publish.source,
        dataSchema = topic.dataSchema,
        kafkaProperties = producerProperties
    )
}

/**
 * Creates a [RoninConsumer] capable of publishing to the Kafka [topic] represented by [topicName].
 */
fun createConsumer(
    topic: KafkaTopic,
    typeMap: Map<String, KClass<*>>,
    kafkaConfig: KafkaConfig,
): RoninConsumer {
    val kafkaProperties = kafkaConfig.properties
    val consumerProperties = RoninConsumerKafkaProperties(
        "bootstrap.servers" to kafkaConfig.bootstrap.servers,
        "security.protocol" to kafkaProperties.security.protocol,
        "sasl.mechanism" to kafkaProperties.sasl.mechanism,
        "sasl.jaas.config" to kafkaProperties.sasl.jaas.config,
        "group.id" to kafkaConfig.retrieve.groupId
    )
    return RoninConsumer(
        topics = listOf(topic.topicName),
        typeMap = typeMap,
        kafkaProperties = consumerProperties
    )
}

data class KafkaEvent<T>(
    private val domain: String,
    private val resource: String,
    private val action: KafkaAction,
    private val resourceId: String,
    val data: T
) {
    private val base = "ronin.$domain.$resource"
    val type = "$base.${action.type}"
    val subject = "$base/$resourceId"
}

enum class KafkaAction(val type: String) {
    PUBLISH("publish"),
    RETRIEVE("retrieve")
}
