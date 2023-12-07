package com.projectronin.interop.queue.model
import com.projectronin.interop.common.hl7.EventType
import com.projectronin.interop.common.hl7.MessageType as HL7MessageType

/**
 * Definition of an API Message.
 * @property id The opaque ID for the message. This identifier should be set by the queue itself, so any value populated when sending a message will be ignored. The ID is guaranteed to be populated when read from the queue.
 * @property messageType The type of message.
 * @property hl7Type The type of HL7 Event.
 * @property hl7Event The specific HL7 event.
 * @property tenant The tenant for which the message was created.
 * @property text The raw text of the message.
 */
data class HL7Message(
    override val id: String? = null,
    override val tenant: String,
    override val text: String,
    val hl7Type: HL7MessageType,
    val hl7Event: EventType,
) : Message {
    override val messageType = MessageType.HL7
}
