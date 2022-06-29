package com.webflux.sse.sample.controller

import org.springframework.http.codec.ServerSentEvent
import reactor.core.publisher.Sinks

data class ConcurrentViewer(
    val viewers: HashSet<String>,
    val sink: Sinks.Many<ServerSentEvent<Set<String>>>
) {
    fun addViewer(name: String): ConcurrentViewer {
        val added = this.copy(viewers = viewers.apply { this.add(name) })
        emitEvent(added.viewers)
        return added
    }

    fun removeViewer(name: String): ConcurrentViewer {
        val removed = this.copy(viewers = viewers.apply { this.remove(name) })
        emitEvent(removed.viewers)
        return removed
    }

    private fun emitEvent(viewers: Set<String>) {
        sink.emitNext(
            ServerSentEvent.builder<Set<String>>()
                .event("changes")
                .data(viewers).build(),
            Sinks.EmitFailureHandler.FAIL_FAST
        )
    }

    companion object {
        fun getFirstViewer(): ConcurrentViewer {
            val sink = Sinks.many().replay().latest<ServerSentEvent<Set<String>>>()
            return ConcurrentViewer(hashSetOf(), sink)
        }
    }
}
