package com.bluetriangle.analytics

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils

internal object MetadataReader {
    private const val SITE_ID = "com.blue-triangle.site-id"
    private const val DEBUG = "com.blue-triangle.debug"
    private const val DEBUG_LEVEL = "com.blue-triangle.debug.level"
    private const val MAX_CACHE_ITEMS = "com.blue-triangle.cache.max-items"
    private const val MAX_RETRY_ATTEMPTS = "com.blue-triangle.cache.max-retry-attempts"
    private const val PERFORMANCE_MONITOR_ENABLE = "com.blue-triangle.performance-monitor.enable"
    private const val PERFORMANCE_MONITOR_INTERVAL = "com.blue-triangle.performance-monitor.interval-ms"
    private const val TRACK_CRASHES_ENABLE = "com.blue-triangle.track-crashes.enable"
    private const val NETWORK_SAMPLE_RATE = "com.blue-triangle.sample-rate.network"

    fun applyMetadata(context: Context, configuration: BlueTriangleConfiguration) {
        try {
            val metadata = getMetadata(context)
            if (metadata != null) {
                val siteId = readString(metadata, SITE_ID, configuration.siteId)
                if (TextUtils.isEmpty(siteId)) {
                    configuration.logger?.error("No site ID")
                } else {
                    configuration.siteId = siteId
                }
                configuration.isDebug = readBool(metadata, DEBUG, configuration.isDebug)
                configuration.debugLevel = readInt(metadata, DEBUG_LEVEL, configuration.debugLevel)
                configuration.maxCacheItems = readInt(metadata, MAX_CACHE_ITEMS, configuration.maxCacheItems)
                configuration.maxAttempts = readInt(metadata, MAX_RETRY_ATTEMPTS, configuration.maxAttempts)
                configuration.isPerformanceMonitorEnabled = readBool(metadata, PERFORMANCE_MONITOR_ENABLE, configuration.isPerformanceMonitorEnabled)
                configuration.performanceMonitorIntervalMs = readLong(metadata, PERFORMANCE_MONITOR_INTERVAL, configuration.performanceMonitorIntervalMs)
                configuration.isTrackCrashesEnabled = readBool(metadata, TRACK_CRASHES_ENABLE, configuration.isTrackCrashesEnabled)
                configuration.networkSampleRate = readDouble(metadata, NETWORK_SAMPLE_RATE, configuration.networkSampleRate)
            }
        } catch (e: Throwable) {
            configuration.logger?.error(e, "Error reading metadata configuration")
        }
    }

    @Throws(PackageManager.NameNotFoundException::class)
    private fun getMetadata(context: Context): Bundle? {
        val app = context.packageManager
            .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        return app.metaData
    }

    private fun readBool(metadata: Bundle, key: String, defaultValue: Boolean): Boolean {
        return metadata.getBoolean(key, defaultValue)
    }

    private fun readString(metadata: Bundle, key: String, defaultValue: String?): String? {
        return metadata.getString(key, defaultValue)
    }

    private fun readInt(metadata: Bundle, key: String, defaultValue: Int): Int {
        return metadata.getInt(key, defaultValue)
    }

    private fun readDouble(metadata: Bundle, key: String, defaultValue: Double): Double {
        // manifest meta-data only reads float
        val value = metadata.getFloat(key, -1f).toDouble()
        return if (value < 0) {
            defaultValue
        } else value
    }

    private fun readLong(metadata: Bundle, key: String, defaultValue: Long): Long {
        // manifest meta-data only reads int if the value is not big enough
        return metadata.getInt(key, defaultValue.toInt()).toLong()
    }
}