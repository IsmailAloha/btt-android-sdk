package com.bluetriangle.analytics.screenTracking.componentcollector

import com.bluetriangle.analytics.Constants.TIMER_MIN_PGTM
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.screenTracking.grouping.BTTChildView

internal class ScreenComponentCollector {

    private val components = arrayListOf<ScreenComponent>()

    fun collect(timer: Timer) {
        Tracker.instance?.mostRecentCustomTimer?.let {
            val childPageName = timer.getField(Timer.FIELD_PAGE_NAME)?:""
            val childPgTm = timer.pageTimeCalculator().toString()
            val childNativeAppProp = timer.nativeAppProperties
            val childLoadStartTime = timer.start
            val childLoadEndTime = childNativeAppProp.loadEndTime

            BTTChildView(
                childNativeAppProp.className,
                childPageName,
                childPgTm,
                (childLoadStartTime - it.nst).toString(),
                (childLoadEndTime - it.nst).toString(),
                childNativeAppProp,
                timer.performanceMonitor?.wcdPerformanceReport
            )
        }
    }

    fun submit() {
        if(components.isEmpty()) return

        Tracker.instance?.apply {
            mostRecentCustomTimer?.let {
                trackerExecutor.submit(ScreenComponentRunnable(configuration, it, ArrayList(components)))
                components.clear()
            }
        }
    }
}