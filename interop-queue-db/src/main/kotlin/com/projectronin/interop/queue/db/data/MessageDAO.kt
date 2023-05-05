package com.projectronin.interop.queue.db.data

import com.projectronin.interop.common.hl7.EventType
import com.projectronin.interop.common.hl7.MessageType
import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.queue.db.data.binding.ApiMessageDOs
import com.projectronin.interop.queue.db.data.binding.HL7MessageDOs
import com.projectronin.interop.queue.model.ApiMessage
import com.projectronin.interop.queue.model.HL7Message
import com.projectronin.interop.queue.model.Message
import com.projectronin.interop.queue.model.QueueStatus
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.asc
import org.ktorm.dsl.associateBy
import org.ktorm.dsl.batchUpdate
import org.ktorm.dsl.count
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.groupBy
import org.ktorm.dsl.isNull
import org.ktorm.dsl.limit
import org.ktorm.dsl.map
import org.ktorm.dsl.min
import org.ktorm.dsl.orderBy
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.schema.ColumnDeclaring
import org.ktorm.support.mysql.LockingMode
import org.ktorm.support.mysql.bulkInsert
import org.ktorm.support.mysql.locking
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime

/**
 * Provides data access operations for message data models.
 */
@Repository
class MessageDAO(@Qualifier("queue") private val database: Database) {
    val logger = KotlinLogging.logger { }

    /**
     * Reads unread [ApiMessage] messages for [resourceType] within [tenant].
     * The number of messages is controlled by the provided [limit].
     * If no limit is provided, then all unread messages will be included.
     * All messages will be returned in descending order, with the oldest queued message first.
     */
    fun readApiMessages(
        tenant: String,
        resourceType: ResourceType,
        limit: Int? = null
    ): List<ApiMessage> {
        logger.info { "Reading ${limit ?: "All"} $resourceType messages from the API queue for $tenant" }
        var query = database.from(ApiMessageDOs).select()
            .where(
                (ApiMessageDOs.resourceType eq resourceType)
                    and (ApiMessageDOs.tenant eq tenant)
                    and (ApiMessageDOs.readInstant.isNull())
            )
            .orderBy(ApiMessageDOs.createInstant.asc())
        query = limit?.let { query.limit(it) } ?: query
        val apiMessages = query.locking(LockingMode.FOR_UPDATE).map { ApiMessageDOs.createEntity(it) }

        if (apiMessages.isNotEmpty()) {
            // Stamp the read instant on all read messages
            val readInstant = Instant.now()
            database.batchUpdate(ApiMessageDOs) {
                apiMessages.forEach { message ->
                    item {
                        set(it.readInstant, readInstant)
                        where {
                            it.id eq message.id
                        }
                    }
                }
            }
        }

        val messages = apiMessages.map {
            ApiMessage(
                it.id.toString(),
                it.tenant,
                it.text,
                it.resourceType,
                com.projectronin.event.interop.internal.v1.Metadata("db${it.id}", OffsetDateTime.now())
            )
        }

        logger.info { "${messages.size} $resourceType messages found." }
        return messages
    }

    /**
     * Reads unread [HL7Message] messages for [MessageType] within [tenant].
     * Messages can be further restricted by [EventType] with all included by default.
     * The number of messages is controlled by the provided [limit].
     * If no limit is provided, then all unread messages will be included.
     * All messages will be returned in descending order, with the oldest queued message first.
     */
    fun readHL7Messages(
        tenant: String,
        type: MessageType,
        event: EventType? = null,
        limit: Int? = null
    ): List<HL7Message> {
        logger.info { "Reading ${limit ?: "All"} [${event ?: type}] messages from the Hl7 queue for $tenant" }

        var query = database.from(HL7MessageDOs).select()
            .where {
                val conditions = ArrayList<ColumnDeclaring<Boolean>>()
                conditions += HL7MessageDOs.hl7Type eq type
                conditions += HL7MessageDOs.tenant eq tenant
                conditions += HL7MessageDOs.readInstant.isNull()
                if (event != null) {
                    conditions += HL7MessageDOs.hl7Event eq event
                }
                conditions.reduce { a, b -> a and b }
            }
            .orderBy(HL7MessageDOs.createInstant.asc())
        query = limit?.let { query.limit(it) } ?: query
        val hl7Messages = query.locking(LockingMode.FOR_UPDATE).map { HL7MessageDOs.createEntity(it) }

        if (hl7Messages.isNotEmpty()) {
            val readInstant = Instant.now()
            database.batchUpdate(HL7MessageDOs) {
                hl7Messages.forEach { message ->
                    item {
                        set(it.readInstant, readInstant)
                        where {
                            it.id eq message.id
                        }
                    }
                }
            }
        }

        val messages = hl7Messages.map {
            HL7Message(
                it.id.toString(),
                it.tenant,
                it.text,
                it.hl7Type,
                it.hl7Event
            )
        }
        logger.info { "${messages.size} $event messages found." }
        return messages
    }

    /**
     * Inserts the supplied [messages] into the database.
     */
    fun insertMessages(messages: List<Message>) {
        logger.info { "Adding ${messages.size} messages to DB queue" }

        val messagesByType = messages.groupBy { it.javaClass.kotlin }

        for ((type, typedMessages) in messagesByType) {
            @Suppress("UNCHECKED_CAST")
            when (type) {
                HL7Message::class -> insertHl7Messages(typedMessages as List<HL7Message>)
                ApiMessage::class -> insertAPIMessages(typedMessages as List<ApiMessage>)
            }
        }
        logger.info { "Messages successfully added to queue" }
    }

    /**
     * Returns the current status of the queue.
     */
    fun getStatus(): QueueStatus {
        val count = count().aliased("count")

        logger.info { "Reading the status of the API queue" }
        val minApiAge = min(ApiMessageDOs.createInstant).aliased("minApiQueue")

        // Create api queue map of tenant to depth and age
        val apiQueueStatus = database
            .from(ApiMessageDOs)
            .select(ApiMessageDOs.tenant, count, minApiAge)
            .where {
                ApiMessageDOs.readInstant.isNull()
            }
            .groupBy(ApiMessageDOs.tenant)
            .associateBy(
                { it[ApiMessageDOs.tenant]!! },
                {
                    val duration = Duration.between(it.getInstant(minApiAge.declaredName!!), Instant.now())
                    Pair(it.getInt(count.declaredName!!), duration.seconds.toInt())
                }
            )

        logger.info { "Reading the status of the HL7 queue" }
        val minHl7Age = min(HL7MessageDOs.createInstant).aliased("minHl7Queue")

        // Create hl7 queue map of tenant to depth and age
        val hl7QueueStatus = database
            .from(HL7MessageDOs)
            .select(HL7MessageDOs.tenant, count, minHl7Age)
            .where {
                HL7MessageDOs.readInstant.isNull()
            }
            .groupBy(HL7MessageDOs.tenant)
            .associateBy(
                { it[HL7MessageDOs.tenant]!! },
                {
                    val duration = Duration.between(it.getInstant(minHl7Age.declaredName!!), Instant.now())
                    Pair(it.getInt(count.declaredName!!), duration.seconds.toInt())
                }
            )

        // Value is a pair with queue depth first and age second
        return QueueStatus(
            apiDepth = apiQueueStatus.mapValues { it.value.first },
            apiAge = apiQueueStatus.mapValues { it.value.second },
            hl7Depth = hl7QueueStatus.mapValues { it.value.first },
            hl7Age = hl7QueueStatus.mapValues { it.value.second }
        )
    }

    private fun insertAPIMessages(apiMessages: List<ApiMessage>) {
        logger.info { "Adding ${apiMessages.size} API messages to DB queue" }

        val createInstant = Instant.now()
        database.bulkInsert(ApiMessageDOs) {
            apiMessages.forEach { message ->
                item {
                    set(it.resourceType, message.resourceType)
                    set(it.tenant, message.tenant)
                    set(it.text, message.text)
                    set(it.createInstant, createInstant)
                }
            }
        }
    }

    private fun insertHl7Messages(hl7Messages: List<HL7Message>) {
        logger.info { "Adding ${hl7Messages.size} Hl7 messages to DB queue" }

        val createInstant = Instant.now()
        database.bulkInsert(HL7MessageDOs) {
            hl7Messages.forEach { message ->
                item {
                    set(it.hl7Type, message.hl7Type)
                    set(it.hl7Event, message.hl7Event)
                    set(it.tenant, message.tenant)
                    set(it.text, message.text)
                    set(it.createInstant, createInstant)
                }
            }
        }
    }
}
