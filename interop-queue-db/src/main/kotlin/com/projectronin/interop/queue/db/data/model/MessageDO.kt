package com.projectronin.interop.queue.db.data.model

import java.time.Instant
/**
 Entity definition for shared values for all Messages
 */
interface MessageDO {
    val id: Int
    val tenant: String
    val text: String
    val createInstant: Instant
    val readInstant: Instant
}
