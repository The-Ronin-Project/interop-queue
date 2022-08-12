package com.projectronin.interop.queue.db

import com.github.database.rider.core.api.connection.ConnectionHolder
import com.github.database.rider.core.api.dataset.DataSet
import com.projectronin.interop.common.test.database.dbrider.DBRiderConnection
import com.projectronin.interop.common.test.database.ktorm.KtormHelper
import com.projectronin.interop.common.test.database.liquibase.LiquibaseTest
import com.projectronin.interop.queue.db.data.binding.ApiMessageDOs
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.ktorm.database.asIterable
import org.ktorm.dsl.count
import org.ktorm.dsl.from
import org.ktorm.dsl.groupBy
import org.ktorm.dsl.isNull
import org.ktorm.dsl.select
import org.ktorm.dsl.where

/**
 * These tests aren't 100% necessary, but I wanted to make sure the indexes I added were actually there and
 * getting used.
 */
@LiquibaseTest(changeLog = "queue/db/changelog/queue.db.changelog-master.yaml")
class SchemaTest {
    @DBRiderConnection
    lateinit var connectionHolder: ConnectionHolder

    @Test
    @DataSet(value = ["/dbunit/message/api/read/ApiAndHl7Messages.yaml"], cleanAfter = true)
    fun `ensure read_instant indexes are created`() {
        val db = KtormHelper.database()

        val apiIndexes = db.useConnection { connection ->
            val sql = "SHOW INDEXES FROM io_queue_api"
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().asIterable().map {
                    it.getString("Key_name")
                }
            }
        }

        val hl7Indexes = db.useConnection { connection ->
            val sql = "SHOW INDEXES FROM io_queue_hl7"
            connection.prepareStatement(sql).use { statement ->
                statement.executeQuery().asIterable().map {
                    it.getString("Key_name")
                }
            }
        }

        assertTrue(apiIndexes.contains("idx_api_read_instant"))
        assertTrue(hl7Indexes.contains("idx_hl7_read_instant"))
    }

    @Test
    @DataSet(value = ["/dbunit/message/api/read/ApiAndHl7Messages.yaml"], cleanAfter = true)
    fun `ensure read_instant indexes are used`() {
        val db = KtormHelper.database()

        // Similar query to the one used to pull queue depth, taken from [MessageDAO]
        val query = db
            .from(ApiMessageDOs)
            .select(ApiMessageDOs.tenant, count())
            .where {
                ApiMessageDOs.readInstant.isNull()
            }
            .groupBy(ApiMessageDOs.tenant)

        // The actual native SQL query
        val sql = query.sql

        // Run an explain plan on the query and pull out the keys used
        val keys = db.useConnection { connection ->
            connection.prepareStatement("EXPLAIN $sql").use { statement ->
                statement.executeQuery().asIterable().map { it.getString("key") }
            }
        }

        assertTrue(keys.contains("idx_api_read_instant"))
    }
}
