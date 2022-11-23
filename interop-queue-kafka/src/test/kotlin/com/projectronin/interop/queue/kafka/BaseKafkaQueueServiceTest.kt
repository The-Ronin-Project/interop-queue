package com.projectronin.interop.queue.kafka

import com.projectronin.interop.kafka.config.KafkaBootstrapConfig
import com.projectronin.interop.kafka.config.KafkaCloudConfig
import com.projectronin.interop.kafka.config.KafkaConfig
import com.projectronin.interop.kafka.config.KafkaPropertiesConfig
import com.projectronin.interop.kafka.config.KafkaPublishConfig
import com.projectronin.interop.kafka.config.KafkaRetrieveConfig
import com.projectronin.interop.kafka.config.KafkaSaslConfig
import com.projectronin.interop.kafka.config.KafkaSaslJaasConfig
import com.projectronin.interop.kafka.config.KafkaSecurityConfig
import com.projectronin.kafka.RoninConsumer
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.File

abstract class BaseKafkaQueueServiceTest {
    companion object {
        val docker =
            DockerComposeContainer(File(BaseKafkaQueueServiceTest::class.java.getResource("/docker-compose-kafka.yaml")!!.file)).waitingFor(
                "kafka",
                Wait.forLogMessage(".*\\[KafkaServer id=\\d+\\] started.*", 1)
            ).start()

        private val consumersByTopic = mutableMapOf<String, RoninConsumer>()
    }
    private val tenantId = "ronin"
    private val cloudConfig = KafkaCloudConfig(
        vendor = "local",
        region = "local"
    )

    protected val kafkaConfig = KafkaConfig(
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
}
