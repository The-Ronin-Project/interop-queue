package com.projectronin.interop.queue.db.data.model

import com.projectronin.interop.common.resource.ResourceType
import org.ktorm.entity.Entity

/**
 * Entity definition for an API Message data object
 */
interface ApiMessageDO : MessageDO, Entity<ApiMessageDO> {
    val resourceType: ResourceType
}
