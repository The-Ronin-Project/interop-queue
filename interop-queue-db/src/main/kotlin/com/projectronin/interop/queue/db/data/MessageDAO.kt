package com.projectronin.interop.queue.db.data

import com.projectronin.interop.common.resource.ResourceType
import com.projectronin.interop.queue.db.data.binding.ApiMessageDOs
import com.projectronin.interop.queue.model.Message
import com.projectronin.interop.queue.model.MessageType
import mu.KotlinLogging
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.asc
import org.ktorm.dsl.batchUpdate
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.isNull
import org.ktorm.dsl.limit
import org.ktorm.dsl.map
import org.ktorm.dsl.orderBy
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.support.mysql.LockingMode
import org.ktorm.support.mysql.bulkInsert
import org.ktorm.support.mysql.locking
import org.springframework.stereotype.Repository
import java.time.Instant

/**
 * Provides data access operations for message data models.
 */
@Repository
class MessageDAO(private val database: Database) {
    val logger = KotlinLogging.logger { }

    /**
     * Reads unread [messageType] messages for [resourceType] within [tenant]. The number of messages is controlled by the provided [limit]. If no limit is provided, then all unread messages will be included. All messages will be returned in descending order, with the oldest queued message first.
     */
    fun readMessages(
        tenant: String,
        messageType: MessageType,
        resourceType: ResourceType,
        limit: Int? = null
    ): List<Message> {
        logger.info { "Reading ${limit ?: "All"} $resourceType messages from the $messageType queue for $tenant" }

        val messages = when (messageType) {
            MessageType.API -> readAPIMessages(tenant, resourceType, limit)
            MessageType.HL7 -> TODO("Implement when HL7 message schema defined")
        }

        logger.info { "${messages.size} $resourceType messages found." }
        return messages
    }

    /**
     * Inserts the supplied [messages] into the database.
     */
    fun insertMessages(messages: List<Message>) {
        logger.info { "Adding ${messages.size} messages to DB queue" }

        val messagesByType = messages.groupBy { it.messageType }

        for ((type, typedMessages) in messagesByType) {
            when (type) {
                MessageType.HL7 -> TODO("Implement when HL7 message schema defined")
                MessageType.API -> insertAPIMessages(typedMessages)
            }
        }
        logger.info { "Messages successfully added to queue" }
    }

    private fun readAPIMessages(tenant: String, resourceType: ResourceType, limit: Int?): List<Message> {
        var query = database.from(ApiMessageDOs).select()
            .where((ApiMessageDOs.resourceType eq resourceType) and (ApiMessageDOs.tenant eq tenant) and (ApiMessageDOs.readInstant.isNull()))
            .orderBy(ApiMessageDOs.createInstant.asc())
        query = limit?.let { query.limit(it) } ?: query
        val apiMessages = query.locking(LockingMode.FOR_UPDATE).map { ApiMessageDOs.createEntity(it) }

        // Stamp the read instant on all read messages
        val readInstant = Instant.now()
        database.batchUpdate(ApiMessageDOs) {
            for (message in apiMessages) {
                item {
                    set(it.readInstant, readInstant)
                    where {
                        it.id eq message.id
                    }
                }
            }
        }

        return apiMessages.map {
            Message(
                it.id.toString(), MessageType.API, it.resourceType, it.tenant, it.text
            )
        }
    }

    private fun insertAPIMessages(apiMessages: List<Message>) {
        logger.info { "Adding ${apiMessages.size} API messages to DB queue" }

        val createInstant = Instant.now()
        database.bulkInsert(ApiMessageDOs) {
            for (message in apiMessages) {
                item {
                    set(it.resourceType, message.resourceType)
                    set(it.tenant, message.tenant)
                    set(it.text, message.text)
                    set(it.createInstant, createInstant)
                }
            }
        }
    }
}
