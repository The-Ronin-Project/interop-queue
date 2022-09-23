package com.projectronin.interop.queue.monitor.spring

import com.timgroup.statsd.StatsDClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import javax.sql.DataSource

// We don't normally write tests around this config stuff, but I needed to get the coverage up.
class InteropQueueMonitorConfigTest {
    private val interopQueueMonitorConfig = InteropQueueMonitorConfig()

    @Test
    fun `can create a data source`() {
        val dataSource = interopQueueMonitorConfig.queueDatasource()
        assertInstanceOf(DataSource::class.java, dataSource)
    }

    @Test
    fun `can create a database`() {
        val dataSource = mockk<DataSource>()
        val database = mockk<Database>()

        mockkStatic(Database::class)
        every { Database.connect(dataSource) } returns database

        assertInstanceOf(Database::class.java, interopQueueMonitorConfig.queueDatabase(dataSource))
    }

    @Test
    fun `can create statsD client`() {
        val statsDClient = interopQueueMonitorConfig.statsDClient("localhost")
        assertInstanceOf(StatsDClient::class.java, statsDClient)
    }
}
