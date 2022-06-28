package com.webflux.sse.sample.controller

import reactor.core.publisher.Sinks

data class ConcurrentViewer(
    val viewers: HashSet<String>,
    val sink: Sinks.Many<Set<String>>
) {
    fun addViewer(name: String): ConcurrentViewer {
        val added = this.copy(viewers = viewers.apply{ this.add(name) })

        println("### added : $added")
        emitEvent(added.viewers)
        return added
    }

    fun removeViewer(name: String): ConcurrentViewer {
        val removed = this.copy(viewers = viewers.apply{ this.remove(name) })
        println("### removed : $removed")
        emitEvent(removed.viewers)
        return removed
    }

    private fun emitEvent(watchers: Set<String>) {
        sink.emitNext(watchers, Sinks.EmitFailureHandler.FAIL_FAST)
    }

    companion object {
        fun getFirstViewer(): ConcurrentViewer {
            val sink = Sinks.many().replay().latest<Set<String>>()
            return ConcurrentViewer(hashSetOf(), sink)
        }
    }
}
