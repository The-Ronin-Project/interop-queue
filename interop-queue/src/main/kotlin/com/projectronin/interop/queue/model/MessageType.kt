package com.projectronin.interop.queue.model

/**
 * Enumeration of the types of messages supported.
 */
enum class MessageType {
    /**
     * The message contains details associated with an HL7 message.
     */
    HL7,

    /**
     * The message contains details associated with a call to an API.
     */
    API
}
