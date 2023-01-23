package com.projectronin.interop.queue.db.spring

import com.projectronin.interop.queue.kafka.spring.KafkaQueueSpringConfig
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan("com.projectronin.interop.queue.db")
@Import(KafkaQueueSpringConfig::class)
class DbQueueSpringConfig
