package com.elvarg.plugin.event

import org.joda.time.DateTime

@Suppress("UNUSED_PARAMETER")
class ServerBootEvent (time : DateTime = DateTime.now()) : Event
