package com.webflux.sse.sample.controller

import java.lang.Thread.sleep
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers


@Controller
class ConcurrentController(
    private val viewersMap: ConcurrentHashMap<Long, ConcurrentViewer>
) {

    @GetMapping("/streams/{id}/username/{name}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streams(
        @PathVariable id: String,
        @PathVariable name: String,
    ): Flux<ServerSentEvent<Set<String>>> {
        val goodsId = id.toLong()

        val viewer = viewersMap[goodsId] ?: addConcurrentViewer(goodsId)
        viewer.also {
            viewersMap[goodsId] = it.addViewer(name)
        }

        return viewer.sink.asFlux()
            .mergeWith(ping())
            .log()
            .doOnCancel {
                println("### canceled !! for goodsId : $goodsId")
                viewer.also {
                    viewersMap[goodsId] = it.removeViewer(name)
                }
            }
    }

    private fun ping() = Flux.interval(Duration.ofSeconds(1))
        .map { ServerSentEvent.builder<Set<String>>().event("ping").build() }

    // TODO : post 요청으로 변경 예정
    @GetMapping("/goods/{id}/username/{name}")
    fun detail(
        @PathVariable id: String,
        @PathVariable name: String,
    ): String {
        return "detail"
    }

    private fun addConcurrentViewer(goodsId: Long) =
        ConcurrentViewer.getFirstViewer().apply {
            viewersMap[goodsId] = this
        }
}
