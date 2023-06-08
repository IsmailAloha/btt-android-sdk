package com.bluetriangle.analytics.networkcapture

import com.bluetriangle.analytics.Tracker
import java.net.URI

class CapturedRequest {
    /**
     * entry type. Fixed value of "resource".
     */
    var entryType = ENTRY_TYPE

    /**
     * Page domain without host.
     */
    var domain: String? = null

    /**
     * Subdomain of the fully qualified domain name.
     */
    var host: String? = null
        set(value) {
            field = value
            domain = value?.split(".")?.takeLast(2)?.joinToString(".")
        }

    /**
     * Full request URL. Sets host, domain, and file parameters as well.
     */
    var url: String? = null
        set(value) {
            field = value
            value?.let {
                val parsedUri = URI(it)
                host = parsedUri.host
                file = parsedUri.path.split("/").filter { segment -> segment.isNotEmpty() }.last()
            }
        }

    /**
     * name of file requested
     */
    var file: String? = null

    /**
     * Request start time.
     */
    var startTime: Long = 0

    /**
     * Request end time.
     */
    var endTime: Long = 0

    /**
     * Request duration.
     */
    var duration: Long = 0

    /**
     * The type of file being returned by the request.
     */
    var requestType: RequestType? = null

    /**
     * Decompressed size of content.
     */
    var decodedBodySize: Long = 0

    /**
     * Compressed size of content
     */
    var encodedBodySize: Long = 0

    /**
     * The HTTP status code of the response
     */
    var responseStatusCode: Int? = null

    val payload: Map<String, String?>
        get() {
            val payload = mutableMapOf(
                FIELD_ENTRY_TYPE to entryType,
                FIELD_DOMAIN to domain,
                FIELD_HOST to host,
                FIELD_URL to url,
                FIELD_FILE to file,
                FIELD_START_TIME to startTime.toString(),
                FIELD_END_TIME to endTime.toString(),
                FIELD_DURATION to duration.toString(),
                FIELD_REQUEST_TYPE to requestType?.name,
                FIELD_DECODED_BODY_SIZE to decodedBodySize.toString(),
                FIELD_ENCODED_BODY_SIZE to encodedBodySize.toString(),
            )

            responseStatusCode?.let { code ->
                payload[FIELD_RESPONSE_CODE] = code.toString()
            }

            return payload.toMap()
        }

    /**
     * start the request timer if not already started
     */
    fun start() {
        if (startTime == 0L) {
            startTime = System.currentTimeMillis()
        }
    }

    /**
     * stop the request timer if not already stopped
     */
    fun stop() {
        if (startTime > 0 && endTime == 0L) {
            endTime = System.currentTimeMillis()
            duration = endTime - startTime
        }
    }

    /**
     * Give the captured request the navigation start time so it can report request start and end relative to the
     * navigation start time
     */
    fun setNavigationStart(navigationStart: Long) {
        startTime -= navigationStart
        endTime -= navigationStart
    }

    /**
     * convenience method to submit this captured request to the tracker
     */
    fun submit() {
        Tracker.instance?.submitCapturedRequest(this)
    }

    override fun toString(): String {
        return "CapturedRequest{ $host...$file $requestType $duration $encodedBodySize }"
    }

    companion object {
        private const val ENTRY_TYPE = "resource"
        const val FIELD_ENTRY_TYPE = "e"
        const val FIELD_DOMAIN = "dmn"
        const val FIELD_HOST = "h"
        const val FIELD_URL = "URL"
        const val FIELD_FILE = "f"
        const val FIELD_START_TIME = "sT"
        const val FIELD_END_TIME = "rE"
        const val FIELD_DURATION = "d"
        const val FIELD_REQUEST_TYPE = "i"
        const val FIELD_DECODED_BODY_SIZE = "dz"
        const val FIELD_ENCODED_BODY_SIZE = "ez"
        const val FIELD_RESPONSE_CODE = "rCd"
    }
}