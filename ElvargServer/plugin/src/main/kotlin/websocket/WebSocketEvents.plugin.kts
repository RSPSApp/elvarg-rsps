package com.elvarg.plugin.test

import api.Misc.logger
import com.elvarg.Server
import com.elvarg.net.NetworkConstants
import com.elvarg.plugin.event.EventListener.Companion.on
import com.elvarg.plugin.event.impl.ServerBootEvent
import com.elvarg.plugin.event.impl.ServerStoppedEvent
import com.elvarg.util.Misc
import org.apache.commons.lang.SystemUtils
import org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS
import java.io.File
import java.io.IOException
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// Register an event listener for ServerBootEvent
on<ServerBootEvent> {
    where { operatingSystemSupported() && NetworkConstants.ENABLE_WEBSOCKET_PROXY }
    then {
        startupWebSocket()
    }
}

// Check if the operating system is supported
fun operatingSystemSupported(): Boolean {
    if (IS_OS_WINDOWS) {
        return true
    }
    println("The WebSocket -> RS proxy is supported on [${SystemUtils.OS_NAME},${SystemUtils.OS_ARCH},${SystemUtils.OS_VERSION}]")
    return false
}

// Start the WebSocket proxy
fun startupWebSocket() {
    val timeout = Duration.ofSeconds(3)
    val executor = Executors.newScheduledThreadPool(1)

    val webSocketProxyPath = File(Misc.getUsersProjectRootDirectory().toFile(), "proxy/web-rs-socket.exe")

    try {
        val proxyProcessBuilder = ProcessBuilder(webSocketProxyPath.absolutePath).directory(File("C:/"))
        executor.schedule({
            try {
                val websocketProxyProcess = proxyProcessBuilder.start()
                println("Started WebSocket > RS Proxy on port 49595")
                Server.websocketProxyProcess = websocketProxyProcess
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }, timeout.toMillis(), TimeUnit.MILLISECONDS)
    } catch (e: IOException) {
        e.printStackTrace()
        throw RuntimeException(e)
    }
}

// Register an event listener for ServerStoppedEvent
on<ServerStoppedEvent> {
    then { println("Server Stopped") }
}