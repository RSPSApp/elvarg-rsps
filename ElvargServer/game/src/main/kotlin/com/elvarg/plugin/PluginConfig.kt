package com.elvarg.plugin

import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports

class PluginConfig : ScriptCompilationConfiguration({
    defaultImports(
        "com.elvarg.plugin.*",
        "com.elvarg.plugin.event.EventListener.Companion.on"
    )
})