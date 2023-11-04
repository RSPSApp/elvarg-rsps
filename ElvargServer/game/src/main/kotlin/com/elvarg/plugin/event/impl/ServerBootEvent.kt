package com.elvarg.plugin.event.impl

import com.elvarg.plugin.event.Event
import org.joda.time.DateTime

/*
 * Called when the server is started but this waits for all events to be finshed before moving on
 */

class ServerBootEvent(val time : DateTime = DateTime.now()) : Event