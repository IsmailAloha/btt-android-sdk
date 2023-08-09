package com.bluetriangle.analytics.monitor

import android.os.Build
import android.os.SystemClock
import android.system.Os
import android.system.OsConstants
import android.util.Log
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.PerformanceReport
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

/**
 * CPU monitoring adapted from https://eng.lyft.com/monitoring-cpu-performance-of-lyfts-android-applications-4e36fafffe12
 */
internal class CpuMonitor(configuration: BlueTriangleConfiguration): MetricMonitor {

    var totalClockTicsLastSecond = 0L
    var elapsedTimeLastSecond = 0L

    companion object {
        private val CPU_STATS_FILE = File("/proc/${android.os.Process.myPid()}/stat")
    }

    override val metricFields: Map<String, String>
        get() = mapOf(
            PerformanceReport.FIELD_MIN_CPU to minCpu.toString(),
            PerformanceReport.FIELD_MAX_CPU to maxCpu.toString(),
            PerformanceReport.FIELD_AVG_CPU to calculateAverageCpu().toString()
        )

    private val logger = configuration.logger
    private var minCpu = Double.MAX_VALUE
    private var maxCpu = 0.0
    private var cumulativeCpu = 0.0
    private var cpuCount: Long = 0

    private val clockSpeedHz = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Os.sysconf(OsConstants._SC_CLK_TCK)
    } else {
        0
    }

    private fun calculateAverageCpu(): Double {
        return if (cpuCount == 0L) {
            0.0
        } else cumulativeCpu / cpuCount
    }

    private fun updateCpu(cpu: Double) {
        if (cpu < minCpu) {
            minCpu = cpu
        }
        if (cpu > maxCpu) {
            maxCpu = cpu
        }
        cumulativeCpu += cpu
        cpuCount++
    }

    private fun readCpuInfo(): CpuInfo? {
        return try {
            val stats = BufferedReader(FileReader(CPU_STATS_FILE)).use { it.readLine() }
            CpuInfo.fromStats(clockSpeedHz, stats)
        } catch (e: IOException) {
            logger?.error(e, "Error reading CPU info")
            null
        }
    }

    override fun onBeforeSleep() {
        if(totalClockTicsLastSecond == 0L) {
            totalClockTicsLastSecond = readCpuInfo()?.totalTime?:0L
            elapsedTimeLastSecond = SystemClock.elapsedRealtime()
        }
    }

    override fun onAfterSleep() {
        val cpuHz = if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) Os.sysconf(OsConstants._SC_CLK_TCK) else 0
        val cpuInfo = if (cpuHz > 0) readCpuInfo() else return
        val elapsedTime = SystemClock.elapsedRealtime()
        val totalClockTicks = readCpuInfo()?.totalTime?:0L
        if (cpuInfo != null) {
            val timeDelta = elapsedTime - elapsedTimeLastSecond
            val clockTicksDelta = totalClockTicks - totalClockTicsLastSecond
            val cpuUsage = (clockTicksDelta.toFloat()/(timeDelta/1000f * cpuHz))*100f
            totalClockTicsLastSecond = totalClockTicks
            elapsedTimeLastSecond = elapsedTime
            Log.d("BlueTriangle", "CPU Usage: $cpuUsage%, Core #${cpuInfo.cpuCount}, Frequency: ${cpuHz}Hz, clockTicksDelta: $clockTicksDelta")
        }
    }

    private data class CpuInfo(
        val clockSpeedHz: Long, // the number of clock ticks per second, measured in Hertz
        val uptimeSec: Long, // the time since the device booted, measured in seconds.
        val utime: Long, // the amount of time that this process has been scheduled in user mode, measured in clock ticks.
        val stime: Long, // the amount of time that this process has been scheduled in kernel mode, measured in clock ticks.
        val cutime: Long, // the amount of time that this process’ waited-for children have been scheduled in user mode, measured in clock ticks.
        val cstime: Long, // the amount of time that this process’ waited-for children have been scheduled in kernel mode, measured in clock ticks.
        val startTime: Long, // the time the process started after system boot, measured in clock ticks.
        val cpuCount: Int
    ) {
        /**
         * @return the time CPU spent doing work for a given application process, and is measured in seconds.
         */
        val totalTime = utime + stime + cutime + cstime

        val seconds = uptimeSec - (startTime.toFloat() / clockSpeedHz)

        val cpuUsage = 100f * ((totalTime / clockSpeedHz) / seconds)

        companion object {
            fun fromStats(clockSpeedHz: Long, stats: String): CpuInfo {
                val statsList = stats.split(" ")
                return CpuInfo(
                    clockSpeedHz,
                    SystemClock.elapsedRealtime() / 1000,
                    statsList[13].toLong(),
                    statsList[14].toLong(),
                    statsList[15].toLong(),
                    statsList[16].toLong(),
                    statsList[21].toLong(),
                    statsList[38].toInt()
                )
            }
        }
    }
}