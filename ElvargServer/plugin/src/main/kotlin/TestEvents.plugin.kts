package com.elvarg.plugin.test

import com.elvarg.plugin.event.impl.ServerBootEvent
import com.elvarg.plugin.event.impl.ServerStartedEvent
import com.elvarg.plugin.event.impl.ServerStoppedEvent

on<ServerStartedEvent> {
    then { println("PID: $pid") }
}

on<ServerStoppedEvent> {

    then { println("Server Stopped") }
}
