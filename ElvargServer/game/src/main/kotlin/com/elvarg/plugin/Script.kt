package com.elvarg.plugin

import com.elvarg.plugin.event.Event
import com.elvarg.plugin.event.EventListener
import com.elvarg.plugin.event.EventManager
import com.github.michaelbull.logging.InlineLogger
import kotlin.reflect.KClass
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript(fileExtension = "plugin.kts", compilationConfiguration = PluginConfig::class)
abstract class Script  {

	val logger: InlineLogger
		get() = InlineLogger()

	inline fun <reified K : Event> on(config: EventListener<K>.() -> EventListener<K>): EventListener<K> {
		return config.invoke(EventListener(K::class)).submit()
	}

	fun <K : Event> on(type: KClass<K>, config: EventListener<K>.() -> EventListener<K>): EventListener<K> {
		return config.invoke(EventListener(type)).submit()
	}

	fun <K : Event> addFilter(type: KClass<K>, filter: K.() -> Boolean) {
		EventManager.addFilter<K>(type.java, filter)
	}

	companion object {


		/**
		 * Run a code block surrounded by a try/catch that logs any error to the specified error
		 */
		suspend fun safeSuspend(logger: InlineLogger = InlineLogger(), action: suspend () -> Unit) {
			try {
				action()
			} catch (ex: Exception) {
				ex.printStackTrace()
				logger.error { ex.toString() }
			}
		}

		/**
		 * Run a code block surrounded by a try/catch that logs any error to the specified error
		 */
		fun safe(logger: InlineLogger = InlineLogger(), action: () -> Unit) {
			try {
				action()
			} catch (ex: Exception) {
				ex.printStackTrace()
				logger.error { ex.toString() }
			}
		}


		/**
		 * Run a code block surrounded by a try/catch that logs any error to the specified error
		 */
		fun <K> safe(logger: InlineLogger = InlineLogger(), default: K? = null, action: () -> K): K {
			return try {
				action()
			} catch (ex: Exception) {
				ex.printStackTrace()
				logger.error { ex.toString() }
				default!!
			}
		}

	}

}