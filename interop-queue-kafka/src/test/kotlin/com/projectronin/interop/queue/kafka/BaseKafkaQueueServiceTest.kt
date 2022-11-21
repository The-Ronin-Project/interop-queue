package com.projectronin.interop.queue.kafka

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
