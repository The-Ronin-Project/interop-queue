package com.projectronin.interop.queue.monitor.spring

import com.timgroup.statsd.NonBlockingStatsDClientBuilder
import com.timgroup.statsd.StatsDClient
import org.ktorm.database.Database
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class InteropQueueMonitorConfig {
    @Bean
    @Qualifier("queue")
    @ConfigurationProperties(prefix = "spring.queue.datasource")
    fun queueDatasource(): DataSource = DataSourceBuilder.create().build()

    /**
     * The returns [Database] for the interop-queue.
     * See Also: [Bean] and [Qualifier] annotation.
     */
    @Bean
    @Qualifier("queue")
    fun queueDatabase(@Qualifier("queue") queueDatasource: DataSource): Database = Database.connect(queueDatasource)

    /**
     * StatsDClient used to send metrics to log aggregation service.
     */
    @Bean
    fun statsDClient(
        @Value("\${dd.agent.host:localhost}") ddAgentHost: String = "localhost",
        @Value("\${dd.agent.port:8125}") ddAgentPort: Int = 8125
    ): StatsDClient = NonBlockingStatsDClientBuilder()
        .prefix("interop-queue")
        .hostname(ddAgentHost)
        .port(ddAgentPort)
        .constantTags("service:interop-queue-monitor")
        .build()
}
