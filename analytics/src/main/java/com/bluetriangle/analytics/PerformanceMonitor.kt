package com.bluetriangle.analytics

import android.os.Process
import com.bluetriangle.analytics.monitor.CpuMonitor
import com.bluetriangle.analytics.monitor.MainThreadMonitor
import com.bluetriangle.analytics.monitor.MemoryMonitor

/**
 * CPU monitoring adapted from https://eng.lyft.com/monitoring-cpu-performance-of-lyfts-android-applications-4e36fafffe12
 */
class PerformanceMonitor(configuration: BlueTriangleConfiguration) : Thread(THREAD_NAME) {
    private val logger = configuration.logger
    private var isRunning = true
    private val interval = configuration.performanceMonitorIntervalMs

    private val metricMonitors = listOf(
        CpuMonitor(configuration),
        MemoryMonitor(configuration),
        MainThreadMonitor(configuration)
    )

    override fun run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST)
        while (isRunning) {
            try {
                metricOnBefore()

                if (isRunning) {
                    sleep(interval)
                }

                metricOnAfter()
            } catch (e: InterruptedException) {
                logger?.error(e, "Performance Monitor thread interrupted")
            }
        }
    }

    private fun metricOnBefore() {
        metricMonitors.forEach { it.onBeforeSleep() }
    }

    private fun metricOnAfter() {
        metricMonitors.forEach { it.onAfterSleep() }
    }

    fun stopRunning() {
        isRunning = false
    }

    val performanceReport: Map<String, String>
        get() {
            val metrics = hashMapOf<String, String>()
            metricMonitors.forEach { metrics.putAll(it.metricFields) }
            return metrics
        }

    companion object {
        private const val THREAD_NAME = "BTTPerformanceMonitor"
    }
}