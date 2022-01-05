package com.projectronin.interop.queue.model

import com.projectronin.interop.common.resource.ResourceType

/**
 * Definition of a Message.
 * @property id The opaque ID for the message. This identifier should be set by the queue itself, so any value populated when sending a message will be ignored. The ID is guaranteed to be populated when read from the queue.
 * @property messageType The type of message.
 * @property resourceType The type of resource represented within the message.
 * @property tenant The tenant for which the message was created.
 * @property text The raw text of the message.
 */
data class Message(
    val id: String? = null,
    val messageType: MessageType,
    val resourceType: ResourceType,
    val tenant: String,
    val text: String
)
