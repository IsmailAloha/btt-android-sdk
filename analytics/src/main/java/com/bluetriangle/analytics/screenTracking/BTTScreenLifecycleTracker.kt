package com.bluetriangle.analytics.screenTracking

import com.bluetriangle.analytics.Constants.TIMER_MIN_PGTM
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.model.Screen
import com.bluetriangle.analytics.utility.logD

internal class BTTScreenLifecycleTracker(
    private val screenTrackingEnabled: Boolean,
    internal var ignoreScreens: List<String>
) : ScreenLifecycleTracker {

    private var loadTime = hashMapOf<String, Long>()
    private var viewTime = hashMapOf<String, Long>()
    private val timers = hashMapOf<String, Timer>()
    private val TAG = this::class.java.simpleName
    private val pageType = "ScreenTracker"

    override fun onLoadStarted(screen: Screen) {
        if (!screenTrackingEnabled) return
        if (shouldIgnore(screen.name)) return

        logD(TAG, "onLoadStarted: $screen")
        timers[screen.toString()] = Timer(screen.name, pageType).start()
        loadTime[screen.toString()] = System.currentTimeMillis()
    }

    override fun onLoadEnded(screen: Screen) {
        if (!screenTrackingEnabled) return
        if (shouldIgnore(screen.name)) return

        logD(TAG, "onLoadEnded: $screen")
        if (timers[screen.toString()] == null) {
            timers[screen.toString()] = Timer(screen.name, pageType).start()
            loadTime[screen.toString()] = System.currentTimeMillis()
        }
    }

    override fun onViewStarted(screen: Screen) {
        if (!screenTrackingEnabled) return
        if (shouldIgnore(screen.name)) return

        logD(TAG, "onViewStarted: $screen")
        viewTime[screen.toString()] = System.currentTimeMillis()
    }

    override fun onViewEnded(screen: Screen) {
        if (!screenTrackingEnabled) return
        if (shouldIgnore(screen.name)) return

        logD(TAG, "onViewEnded: $screen")
        val scr = screen.toString()
        val timer = timers[scr] ?: return
        val loadTm = loadTime[scr] ?: 0L
        val viewTm = viewTime[scr] ?: 0L
        val disappearTm = System.currentTimeMillis()

        timer.setContentGroupName(pageType)
        timer.pageTimeCalculator = {
            (viewTm - loadTm).coerceAtLeast(TIMER_MIN_PGTM)
        }
        timer.generateNativeAppProperties()
        timer.nativeAppProperties.loadTime = viewTm - loadTm
        timer.nativeAppProperties.fullTime = disappearTm - loadTm
        timer.nativeAppProperties.screenType = screen.type
        timer.end().submit()
        timers.remove(scr)
    }

    private fun shouldIgnore(name: String): Boolean {
        return ignoreScreens.contains(name)
    }

}