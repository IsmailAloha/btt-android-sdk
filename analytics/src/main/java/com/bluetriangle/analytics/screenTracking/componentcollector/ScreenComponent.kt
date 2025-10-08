package com.bluetriangle.analytics.screenTracking.componentcollector

import com.bluetriangle.analytics.model.NativeAppProperties

internal class ScreenComponent(
        val className: String,
        val pageName: String,
        val pageTime: String,
        val startTime: String,
        val endTime: String,
        val nativeAppProperties: NativeAppProperties,
        val performanceMetrics: Map<String, String>?
) {
    companion object{
        const val ENTRY_TYPE = "screen"
    }
}