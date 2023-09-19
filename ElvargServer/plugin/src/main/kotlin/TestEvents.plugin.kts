package com.elvarg.plugin.test

import com.elvarg.plugin.event.EventListener.Companion.on
import com.elvarg.plugin.event.ServerBootEvent

on<ServerBootEvent> {
    then { Beep.hey() }
}
