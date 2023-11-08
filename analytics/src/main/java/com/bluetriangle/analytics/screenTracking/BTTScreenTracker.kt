package com.bluetriangle.analytics.screenTracking

import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.model.Screen
import com.bluetriangle.analytics.model.ScreenType

class BTTScreenTracker(private val pageName: String) {

    private val id = "${pageName}#${System.currentTimeMillis()}"
    var screenType: String = ""

    fun onLoadStarted() {
        Tracker.instance?.screenTrackMonitor?.onLoadStarted(Screen(
            id,
            pageName,
            ScreenType.Custom(screenType)
        ))
    }

    fun onLoadEnded() {
        Tracker.instance?.screenTrackMonitor?.onLoadStarted(Screen(
            id,
            pageName,
            ScreenType.Custom(screenType)
        ))
    }

    fun onViewStarted() {
        Tracker.instance?.screenTrackMonitor?.onLoadStarted(Screen(
            id,
            pageName,
            ScreenType.Custom(screenType)
        ))
    }

    fun onViewEnded() {
        Tracker.instance?.screenTrackMonitor?.onLoadStarted(Screen(
            id,
            pageName,
            ScreenType.Custom(screenType)
        ))
    }
}