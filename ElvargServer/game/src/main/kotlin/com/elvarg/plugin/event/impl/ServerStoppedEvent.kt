package com.elvarg.plugin.event.impl

import com.elvarg.plugin.event.Event
import org.joda.time.DateTime



/*
 * Called when the server is stopped by an update or a command
 */
class ServerStoppedEvent(val time : DateTime = DateTime.now()) : Event