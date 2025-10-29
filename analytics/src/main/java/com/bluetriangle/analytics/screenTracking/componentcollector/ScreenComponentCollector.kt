package com.bluetriangle.analytics.screenTracking.componentcollector

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker

internal class ScreenComponentCollector {

    private val components = arrayListOf<Timer>()

    fun collect(timer: Timer) {
        Tracker.instance?.configuration?.logger?.debug("ScreenComponentCollector: Collecting Timer: ${timer}")
        Tracker.instance?.mostRecentCustomTimer?.let {
            components.add(timer)
        }
    }

    fun submit() {
        if(components.isEmpty()) return

        Tracker.instance?.configuration?.logger?.debug("ScreenComponentCollector: Submitting timers: ${components.size}")
        Tracker.instance?.apply {
            mostRecentCustomTimer?.let {
                val timerMetadata = MostRecentTimerData(
                    it.getField(Timer.FIELD_SITE_ID),
                    it.start,
                    it.getField(Timer.FIELD_TRAFFIC_SEGMENT_NAME),
                    it.getField(Timer.FIELD_CONTENT_GROUP_NAME),
                    it.getField(Timer.FIELD_SESSION_ID),
                    it.getField(Timer.FIELD_PAGE_NAME),
                    it.getField(Timer.FIELD_BROWSER_VERSION),
                    it.getField(Timer.FIELD_DEVICE)
                )
                trackerExecutor.submit(ScreenComponentRunnable(configuration, timerMetadata, it.getScreenComponents()))
                components.clear()
            }
        }
    }

    private fun Timer.getScreenComponents():List<ScreenComponent> {
        return components.map { timer ->
            val childPageName = timer.getField(Timer.FIELD_PAGE_NAME)?:""
            val childNativeAppProp = timer.nativeAppProperties
            val childLoadStartTime = timer.start
            val childLoadEndTime = if(childNativeAppProp.loadEndTime == 0L) System.currentTimeMillis() else childNativeAppProp.loadEndTime

            ScreenComponent(
                childNativeAppProp.className,
                childPageName,
                (childLoadEndTime - childLoadStartTime).toString(),
                (childLoadStartTime - start).toString(),
                (childLoadEndTime - start).toString(),
                childNativeAppProp,
                timer.performanceMonitor?.wcdPerformanceReport
            )
        }
    }
}