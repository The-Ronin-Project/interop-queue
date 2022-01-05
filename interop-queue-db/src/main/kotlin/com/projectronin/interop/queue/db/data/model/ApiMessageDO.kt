package com.projectronin.interop.queue.db.data.model

import com.projectronin.interop.common.resource.ResourceType
import org.ktorm.entity.Entity
import java.time.Instant

/**
 * Entity definition for an API Message data object
 */
interface ApiMessageDO : Entity<ApiMessageDO> {
    val id: Int
    val resourceType: ResourceType
    val tenant: String
    val text: String
    val createInstant: Instant
    val readInstant: Instant
}
