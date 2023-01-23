package com.projectronin.interop.queue.db.spring

import com.projectronin.interop.queue.db.DBQueueService
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.ktorm.database.Database
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DbQueueSpringConfig::class, TestConfig::class])
@TestPropertySource("kafka-test.properties")
class DbQueueSpringConfigTest {
    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun `loads DBQueueService`() {
        val service = applicationContext.getBean<DBQueueService>()
        assertNotNull(service)
        assertInstanceOf(DBQueueService::class.java, service)
    }
}

@Configuration
class TestConfig {
    @Bean
    @Qualifier("queue")
    fun queueDatabase(): Database = mockk()
}
