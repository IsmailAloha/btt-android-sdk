package com.bluetriangle.analytics.monitor

import android.os.Handler
import android.os.Looper
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.anrwatchdog.AnrException

class MainThreadMonitor(configuration: BlueTriangleConfiguration): MetricMonitor {
    private val logger = configuration.logger

    override val metricFields: Map<String, String>
        get() = mapOf()

    private val anrDelay = configuration.anrDelay * 1000L

    private val handler = Handler(Looper.getMainLooper())
    private var dummyTask = Runnable { }
    private var postTime:Long = 0L

    private var maxMainThreadBlock:Long = 0L

    private var isANRNotified = false

    override fun onBeforeSleep() {
        if(!handler.hasMessages(0)) {
            postTime = System.currentTimeMillis()
            handler.postAtFrontOfQueue(dummyTask)
        }
    }

    override fun onAfterSleep() {
        val threadBlockDelay = System.currentTimeMillis() - postTime

        maxMainThreadBlock = maxMainThreadBlock.coerceAtLeast(threadBlockDelay)

        if(threadBlockDelay > anrDelay) {
            if(!isANRNotified) {
                isANRNotified = true
                logger?.debug("Sending ANR as Exception")
                Tracker.instance?.trackException("ANR Detected", AnrException(threadBlockDelay))
            }
        } else {
            isANRNotified = false
        }
    }
}