package com.webflux.sse.sample

import org.junit.jupiter.api.Test
import reactor.core.publisher.Sinks

class SinkTest {

    @Test
    fun sink() {

        val replaySink = Sinks.many().replay().limit<Int>(1)

        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST)
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST)


        val intFlux = replaySink.asFlux()
        intFlux.subscribe { println("Subscriber 1: $it") }

        val intFlux1 = replaySink.asFlux()
        intFlux1.subscribe { println("Subscriber 2: $it") }

        replaySink.tryEmitNext(3)

        val intFlux2 = replaySink.asFlux()
        intFlux2.subscribe { println("Subscriber 3: $it") }
    }
}
