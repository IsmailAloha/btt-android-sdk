package com.bluetriangle.analytics

/**
 * constant values used across the SDK
 */
object Constants {
    const val OS = "Android"
    const val CRASH_PAGE_NAME = "Android Crash"
    const val BROWSER = "Native App"
    const val DEVICE_TABLET = "Tablet"
    const val DEVICE_MOBILE = "Mobile"
    const val UTF_8 = "UTF-8"
    const val METHOD_POST = "POST"
    const val HEADER_USER_AGENT = "User-Agent"
    const val HEADER_CONTENT_TYPE = "Content-Type"
    const val CONTENT_TYPE_JSON = "application/json; charset=utf-8"
    const val CHECK_INTERVAL: Long = 1000
    const val ANR_DEFAULT_INTERVAL: Int = 5 // in seconds
    const val TIMER_MIN_PGTM = 15L

    /**
     * Max length of extended custom variable strings
     */
    const val EXTENDED_CUSTOM_VARIABLE_MAX_LENGTH = 1024

    /**
     * The max size of the extended custom variable JSON payload
     */
    const val EXTENDED_CUSTOM_VARIABLE_MAX_PAYLOAD = 1024 * 1024 * 3  // 3 MB
    const val BUFFER_REPOSITORY = "Buffer"
    const val DEFAULT_NETWORK_SAMPLE_RATE = 0.05
}