package com.bluetriangle.analytics.monitor

import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.CrashRunnable
import com.bluetriangle.analytics.PerformanceReport
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker

internal class MemoryMonitor(val configuration: BlueTriangleConfiguration) : MetricMonitor {

    private val totalMemory = Runtime.getRuntime().maxMemory()

    override val metricFields: Map<String, String>
        get() = mapOf(
            PerformanceReport.FIELD_MIN_MEMORY to minMemory.toString(),
            PerformanceReport.FIELD_TOTAL_MEMORY to totalMemory.toString(),
            PerformanceReport.FIELD_MAX_MEMORY to maxMemory.toString(),
            PerformanceReport.FIELD_AVG_MEMORY to calculateAverageMemory().toString()
        )

    private var minMemory = Long.MAX_VALUE
    private var maxMemory: Long = 0
    private var cumulativeMemory: Long = 0
    private var memoryCount: Long = 0
    private val logger = configuration.logger

    private fun calculateAverageMemory(): Long {
        return if (memoryCount == 0L) {
            0
        } else cumulativeMemory / memoryCount
    }

    private var isMemoryThresholdReached = false

    private val Long.mb: Long
        get() = this / (1024 * 1024)

    override fun onBeforeSleep() {
        val usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        logger?.debug("Used Memory: $usedMemory, Total Memory: $totalMemory")
        if (usedMemory / totalMemory.toFloat() >= 0.8) {
            if (!isMemoryThresholdReached) {
                isMemoryThresholdReached = true
                onThresholdReached(usedMemory.mb, totalMemory.mb)
            }
        } else {
            isMemoryThresholdReached = false
        }
        updateMemory(usedMemory)
    }

    private fun onThresholdReached(usedMemory:Long, totalMemory:Long) {
        configuration.logger?.debug("Memory Warning recieved: Used: ${usedMemory}MB, Total: ${totalMemory}MB")

        val timeStamp = System.currentTimeMillis().toString()
        val mostRecentTimer = Tracker.instance?.getMostRecentTimer()
        val crashHitsTimer: Timer = Timer().startWithoutPerformanceMonitor()
        crashHitsTimer.setPageName(
            mostRecentTimer?.getField(Timer.FIELD_PAGE_NAME)
                ?: Tracker.BTErrorType.MemoryWarning.value
        )
        if (mostRecentTimer != null) {
            mostRecentTimer.generateNativeAppProperties()
            crashHitsTimer.nativeAppProperties = mostRecentTimer.nativeAppProperties
        }
        crashHitsTimer.setError(true)

        try {
            val thread = Thread(
                CrashRunnable(
                    configuration,
                    "Critical memory usage detected. App using ${usedMemory}MB of App's limit ${totalMemory}MB",
                    timeStamp,
                    crashHitsTimer,
                    Tracker.BTErrorType.MemoryWarning
                )
            )
            thread.start()
            thread.join()
        } catch (interruptedException: InterruptedException) {
            interruptedException.printStackTrace()
        }
    }

    private fun updateMemory(memory: Long) {
        if (memory < minMemory) {
            minMemory = memory
        }
        if (memory > maxMemory) {
            maxMemory = memory
        }
        cumulativeMemory += memory
        memoryCount++
    }

    override fun onAfterSleep() {

    }
}