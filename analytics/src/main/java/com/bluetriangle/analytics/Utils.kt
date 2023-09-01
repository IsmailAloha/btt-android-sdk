package com.bluetriangle.analytics

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import com.bluetriangle.analytics.BuildConfig
import java.io.PrintWriter
import java.io.StringWriter
import java.io.UnsupportedEncodingException
import java.io.Writer
import java.security.SecureRandom
import java.util.*

/**
 * Utility methods
 */
internal object Utils {
    /**
     * Return the string resource for the given key or null if not found.
     *
     * @param context application context
     * @param key     name of the identifier to look up
     * @return string resource for the given key or null if not found.
     */
    fun getResourceString(context: Context, key: String): String? {
        val id = getIdentifier(context, "string", key)
        return if (id != 0) {
            context.resources.getString(id)
        } else {
            null
        }
    }

    /**
     * Get the identifier for the resource with a given type and key.
     *
     * @param context application context
     * @param type    resource type, ex: string
     * @param key     name of the identifier to look up
     * @return resource identifier for requested type and key or 0 if not found
     */
    private fun getIdentifier(context: Context, type: String, key: String): Int {
        return context.resources.getIdentifier(key, type, context.packageName)
    }

    /**
     * Determine if this application is debuggable
     *
     * @param context application context
     * @return true if application flag for debugging is set, else false
     */
    fun isDebuggable(context: Context): Boolean {
        try {
            val packageName = context.packageName
            val flags = context.packageManager.getApplicationInfo(packageName, 0).flags
            return flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (ignored: PackageManager.NameNotFoundException) {
        }
        return false
    }

    /**
     * Returns a randomly generated ID that is 18 characters long
     *
     * @return random long
     */
    fun generateRandomId(): String {
        val random = Math.abs(Random().nextLong())
        return String.format("%019d", random).substring(0, 19)
    }

    /**
     * Get the applications package info
     *
     * @param context application context
     * @return application's package info
     */
    private fun getAppPackageInfo(context: Context): PackageInfo? {
        try {
            return context.packageManager
                .getPackageInfo(context.packageName, PackageManager.GET_CONFIGURATIONS)
        } catch (ignore: PackageManager.NameNotFoundException) {
            // this should never happen, we are looking up ourself
        }
        return null
    }

    /**
     * Get the application's version
     *
     * @param context application context
     * @return The application's version or UNKNOWN if not found
     */
    fun getAppVersion(context: Context): String {
        val packageInfo = getAppPackageInfo(context)
        return if (packageInfo != null) {
            packageInfo.versionName
        } else "UNKNOWN"
    }

    val os: String
        get() = "${Constants.OS} ${Build.VERSION.RELEASE}"

    fun isTablet(context: Context): Boolean {
        return context.resources.getBoolean(R.bool.isTablet)
    }

    fun getAppName(context: Context): String {
        return try {
            val applicationInfo = context.applicationInfo
            val appNameStringResourceId = applicationInfo.labelRes
            if (appNameStringResourceId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
                appNameStringResourceId
            )
        } catch (e: Exception) {
            ""
        }
    }

    fun getAppNameAndOs(context: Context): String {
        return "${getAppName(context)} $os"
    }

    /**
     * Builds a user agent string
     * @param context application context
     * @return User-Agent: My-App/1.0 iOS/15.5 (iPhone14,2) btt-swift-sdk/3.1.0
     */
    fun buildUserAgent(context: Context): String {
        val appName = getAppName(context)
        val appVersion = getAppVersion(context)
        return "$appName/$appVersion Android/${Build.VERSION.RELEASE} ($deviceName) btt-android-sdk/${BuildConfig.SDK_VERSION}"
    }

    val deviceName: String
        get() = if (Build.MODEL.startsWith(Build.MANUFACTURER)) {
            capitalize(Build.MODEL)
        } else capitalize(Build.MANUFACTURER) + " " + Build.MODEL

    fun capitalize(str: String?): String {
        return str?.replaceFirstChar { it.uppercaseChar() } ?: ""
    }

    /**
     * Build the base 64 encoded data to POST to the API
     *
     * @return base 64 encoded JSON payload
     * @throws UnsupportedEncodingException if UTF-8 encoding is not supported
     */
    @Throws(UnsupportedEncodingException::class)
    fun b64encode(data: String): ByteArray {
        return Base64.encode(data.toByteArray(), Base64.DEFAULT)
    }

    fun exceptionToStacktrace(message: String?, e: Throwable): String {
        val result: Writer = StringWriter()
        val printWriter = PrintWriter(result)
        e.printStackTrace(printWriter)
        printWriter.close()
        val lines = result.toString().split("\\r?\\n".toRegex())
        return buildString {
            if (!message.isNullOrBlank()) {
                append(message)
                append("~~")
            }
            for (line in lines.take(lines.size - 1)) {
                if (line.isNotBlank()) {
                    append(line)
                    append("~~")
                }
            }
            append(lines.last().trim())
        }
    }

    /**
     * determine if samples should be captured
     * @param sampleRate the rate at which samples should captured
     * @return true if this instance should sample, false otherwise
     */
    fun shouldSample(sampleRate: Double): Boolean {
        val random = SecureRandom()
        return sampleRate >= random.nextDouble()
    }
}


fun Int.trimLevelString() = when (this) {
    Application.TRIM_MEMORY_BACKGROUND -> "TRIM_MEMORY_BACKGROUND"
    Application.TRIM_MEMORY_COMPLETE -> "TRIM_MEMORY_COMPLETE"
    Application.TRIM_MEMORY_MODERATE -> "TRIM_MEMORY_MODERATE"
    Application.TRIM_MEMORY_RUNNING_CRITICAL -> "TRIM_MEMORY_RUNNING_CRITICAL"
    Application.TRIM_MEMORY_RUNNING_LOW -> "TRIM_MEMORY_RUNNING_LOW"
    Application.TRIM_MEMORY_RUNNING_MODERATE -> "TRIM_MEMORY_RUNNING_MODERATE"
    Application.TRIM_MEMORY_UI_HIDDEN -> "TRIM_MEMORY_UI_HIDDEN"
    else -> "UNKNOWN"
}