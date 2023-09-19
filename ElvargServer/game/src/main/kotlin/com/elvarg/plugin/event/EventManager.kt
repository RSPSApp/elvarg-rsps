@file:Suppress("UNCHECKED_CAST")

package com.elvarg.plugin.event

import com.elvarg.plugin.event.EventListener
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

object EventManager {

    /**
     * Coroutine channels that receive and dispatch events
     */
    private val eventChannels = ConcurrentHashMap<Class<out Event>, MutableSharedFlow<out Event>>()

    /**
     * Predicates used to filter events before they are sent to listeners
     */
    private val eventFilter = ConcurrentHashMap<Class<out Event>, LinkedList<Predicate<out Event>>>()

    /**
     * Predicates used to filter events before they are sent to listeners
     */
    private val listeners = ConcurrentHashMap<Class<out Event>, List<EventListener<out Event>>>()

    /**
     * Coroutine context for processing events
     */
    @OptIn(DelicateCoroutinesApi::class)
    private val context = newFixedThreadPoolContext(
        (Runtime.getRuntime().availableProcessors() - 1).coerceAtLeast(1),
        "EventContext"
    )

    /**
     * Post an [Event] to all subscribed [EventListener]s
     */
    fun <E : Event> post(event: E) {

        CoroutineScope(context).launch {
            if (getFilters<E>(event.javaClass).all { it.test(event) }) {
                getChannel<E>(event.javaClass).emit(event)
            }
        }

    }

    /**
     * Post an [Event] to all subscribed [EventListener]s and waits for all subscribers to complete
     */
    fun <E : Event> postAndWait(event: E) {
        runBlocking {
            listeners[event.javaClass]?.map {
                CoroutineScope(context).launch {
                    if (getFilters<E>(event.javaClass).all { filter -> filter.test(event) }) {
                        with(it as EventListener<E>) {
                            if (condition(event)) {
                                action(event)
                            } else {
                                otherwiseAction(event)
                            }
                        }
                    }
                }
            }?.joinAll()
        }
    }

    /**
     * Post an [Event] to all subscribed [EventListener]s and waits for all subscribers to complete
     */
    fun <E : Event> postAndCall(event: E, completion: java.lang.Runnable) {
        CoroutineScope(context).launch { postAndWait(event) }.invokeOnCompletion { completion.run() }
    }

    /**
     * Get the coroutine [Channel] responsible for the specific event
     */
    private fun <E : Event> getChannel(clazz: Class<out Event>): MutableSharedFlow<E> {
        if (!eventChannels.containsKey(clazz)) {
            eventChannels[clazz] = MutableSharedFlow<E>()
        }

        return eventChannels[clazz]!! as MutableSharedFlow<E>
    }

    /**
     * Get the list of filter predicates for the specific event
     */
    private fun <E : Event> getFilters(clazz: Class<out Event>): LinkedList<Predicate<E>> {
        if (!eventFilter.containsKey(clazz)) {
            eventFilter[clazz] = LinkedList<Predicate<E>>() as LinkedList<Predicate<out Event>>
        }

        return (eventFilter[clazz]!!) as LinkedList<Predicate<E>>
    }

    /**
     * Post an [Event] to all subscribed [EventListener]s
     */
    fun <E : Event> addFilter(clazz: Class<E>, filter: Predicate<E>) {
        getFilters<E>(clazz).addLast(filter)
    }

    /**
     * Listen for new posts on the [Channel] on a separate coroutine
     */
    fun <E : Event> listen(event: Class<out Event>, listener: EventListener<E>) {
        val current = listeners[event]

        if (current == null) listeners[event] = listOf(listener)
        else listeners[event] = listOf(listener) + listeners[event] as List<EventListener<out Event>>

        CoroutineScope(context).launch {
            try {
                getChannel<E>(event).collect {
                    try {
                        if (listener.condition(it)) {
                            listener.action(it)
                        } else {
                            listener.otherwiseAction(it)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

}
