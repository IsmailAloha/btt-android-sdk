package com.bluetriangle.analytics.launchtime.helpers

import com.bluetriangle.analytics.launchtime.data.AppEvent
import com.bluetriangle.analytics.launchtime.data.LaunchType
import java.util.LinkedList
import java.util.Queue

internal class AppEventAccumulator {

    private var events:Queue<AppEvent> = LinkedList()

    fun accumulate(event: AppEvent): Result? {
        events.offer(event)
        if(event is AppEvent.ActivityResumed) {
            val appEvents = events.toList()
            events.clear()

            return when (appEvents.first()) {
                is AppEvent.AppCreated -> Result(LaunchType.Cold, appEvents)
                is AppEvent.ActivityStarted -> Result(LaunchType.Hot, appEvents)
                else -> null
            }
        }
        return null
    }

    class Result(val type: LaunchType, val events:List<AppEvent>)
}