package com.projectronin.interop.queue.db.data.binding

import com.projectronin.interop.common.hl7.EventType
import com.projectronin.interop.common.hl7.MessageType
import com.projectronin.interop.queue.db.data.model.HL7MessageDO
import org.ktorm.schema.Table
import org.ktorm.schema.enum
import org.ktorm.schema.int
import org.ktorm.schema.text
import org.ktorm.schema.timestamp
import org.ktorm.schema.varchar

/**
 * Table binding definition for [HL7MessageDO] data objects.
 */
object HL7MessageDOs : Table<HL7MessageDO>("io_queue_hl7") {
    val id = int("io_queue_hl7_id").primaryKey().bindTo { it.id }
    val hl7Type = enum<MessageType>("hl7_type").bindTo { it.hl7Type }
    val hl7Event = enum<EventType>("hl7_event").bindTo { it.hl7Event }
    val tenant = varchar("tenant_mnemonic").bindTo { it.tenant }
    val text = text("text").bindTo { it.text }
    val createInstant = timestamp("create_instant").bindTo { it.createInstant }
    val readInstant = timestamp("read_instant").bindTo { it.readInstant }
}
