package com.elvarg.plugin.event

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class EventListener<E : Event>(val type: KClass<E>) {

    var condition: E.() -> Boolean = { true }

	var action: suspend E.() -> Unit = { }

    var otherwiseAction: E.() -> Unit = { }

    fun where(condition: E.() -> Boolean): EventListener<E> {
        this.condition = condition
        return this
    }

    fun then(plugin: suspend E.() -> Unit): EventListener<E> {
        this.action = plugin
        return this
    }

    fun otherwise(plugin: E.() -> Unit): EventListener<E> {
        this.otherwiseAction = plugin
        return this
    }

    fun submit() : EventListener<E> {
        val oldAction = action

        action = {
            try {
                oldAction()
            } catch (ex : Exception) {
                ex.printStackTrace()
            }
        }

        EventManager.listen(type.java as Class<Event>, this)
        return this
    }

    companion object {

		inline fun <reified K : Event> on(config : EventListener<K>.() -> EventListener<K>) : EventListener<K> {
			return config.invoke(EventListener(K::class)).submit()
		}
    }
}


