package com.webflux.sse.sample.controller

import java.util.concurrent.ConcurrentHashMap
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SseConfig {

    @Bean
    fun concurrentCounters() = ConcurrentHashMap<Long, ConcurrentViewer>()
}
