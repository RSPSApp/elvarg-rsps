package com.elvarg.plugin.event.impl

import com.elvarg.plugin.event.Event
import org.joda.time.DateTime


/*
 * Called when the server is started
 */

class ServerStartedEvent (val time : DateTime = DateTime.now(),val pid : Long = ProcessHandle.current().pid()) : Event