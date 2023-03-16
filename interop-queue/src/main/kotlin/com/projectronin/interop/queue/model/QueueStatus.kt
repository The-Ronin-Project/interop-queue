package com.projectronin.interop.queue.model

/**
 * Data class that represents the status of the queue.
 * [apiDepth] is a map of all tenants with unprocessed messages in the API queue to their queue depth.
 * [hl7Depth] is a map of all tenants with unprocessed messages in the HL7 queue to their queue depth.
 * [apiAge] is a map of all tenants with unprocessed messages in the API queue to the age of their oldest message in seconds.
 * [hl7Age] is a map of all tenants with unprocessed messages in the HL7 queue to the age of their oldest message in seconds.
 */
data class QueueStatus(
    val apiDepth: Map<String, Int>,
    val hl7Depth: Map<String, Int>,
    val apiAge: Map<String, Int>,
    val hl7Age: Map<String, Int>
)
