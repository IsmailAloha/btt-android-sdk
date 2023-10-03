package com.bluetriangle.analytics.monitor

import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.BuildConfig
import com.bluetriangle.analytics.CrashRunnable
import com.bluetriangle.analytics.PerformanceReport
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import java.lang.RuntimeException

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
    private var memoryWarningException: MemoryWarningException? = null
    private var memoryUsed = arrayListOf<Long>()

    private fun calculateAverageMemory(): Long {
        return if (memoryCount == 0L) {
            0
        } else cumulativeMemory / memoryCount
    }

    private var isMemoryThresholdReached = false

    private val Long.mb: Long
        get() = this / (1024 * 1024)

    class MemoryWarningException(val usedMemory: Long, val totalMemory: Long) : RuntimeException("Critical memory usage detected. App using ${usedMemory}MB of App's limit ${totalMemory}MB") {
        var count: Int = 1
    }

    override fun onBeforeSleep() {
        val usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        logger?.debug("Used Memory: $usedMemory (${usedMemory.mb}MB), Total Memory: $totalMemory (${totalMemory.mb}MB)")
        if (usedMemory / totalMemory.toFloat() >= 0.8) {
            if (!isMemoryThresholdReached && configuration.isMemoryWarningEnabled) {
                isMemoryThresholdReached = true
                if(memoryWarningException == null) {
                    memoryWarningException = MemoryWarningException(usedMemory.mb, totalMemory.mb)
                } else {
                    memoryWarningException?.count = (memoryWarningException?.count?:1) + 1
                }
            }
        } else {
            isMemoryThresholdReached = false
        }
        updateMemory(usedMemory)
    }

    private fun onThresholdReached(memoryWarningException: MemoryWarningException) {
        configuration.logger?.debug("Memory Warning received ${memoryWarningException.count} times: Used: ${memoryWarningException.usedMemory}MB, Total: ${memoryWarningException.totalMemory}MB")

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
                    memoryWarningException.message?:"",
                    timeStamp,
                    crashHitsTimer,
                    Tracker.BTErrorType.MemoryWarning,
                    errorCount = memoryWarningException.count
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
        memoryUsed.add(memory)
        cumulativeMemory += memory
        memoryCount++
    }

    override fun onAfterSleep() {

    }

    override fun onTimerSubmit(pageName: String) {
        super.onTimerSubmit(pageName)
        memoryWarningException?.let {
            onThresholdReached(it)
        }
        if(BuildConfig.DEBUG) {
            logger?.debug("MemoryUsed in $pageName : $memoryUsed, Max: ${maxMemory}, Min: ${minMemory}")
        }
    }
}