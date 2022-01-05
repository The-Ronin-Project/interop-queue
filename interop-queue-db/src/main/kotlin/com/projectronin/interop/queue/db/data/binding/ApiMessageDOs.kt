package com.projectronin.interop.queue.db.data.binding

import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.queue.db.data.model.ApiMessageDO
import org.ktorm.schema.Table
import org.ktorm.schema.enum
import org.ktorm.schema.int
import org.ktorm.schema.text
import org.ktorm.schema.timestamp
import org.ktorm.schema.varchar

/**
 * Table binding definition for [ApiMessageDO] data objects.
 */
object ApiMessageDOs : Table<ApiMessageDO>("io_queue_api") {
    val id = int("io_queue_api_id").primaryKey().bindTo { it.id }
    val resourceType = enum<ResourceType>("resource_type").bindTo { it.resourceType }
    val tenant = varchar("tenant_mnemonic").bindTo { it.tenant }
    val text = text("text").bindTo { it.text }
    val createInstant = timestamp("create_instant").bindTo { it.createInstant }
    val readInstant = timestamp("read_instant").bindTo { it.readInstant }
}
