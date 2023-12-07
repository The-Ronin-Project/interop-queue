package com.projectronin.interop.queue.db.data.model

import com.projectronin.interop.common.hl7.EventType
import com.projectronin.interop.common.hl7.MessageType
import org.ktorm.entity.Entity

/**
 * Entity definition for an API Message data object
 */
interface HL7MessageDO : MessageDO, Entity<HL7MessageDO> {
    val hl7Type: MessageType
    val hl7Event: EventType
}
