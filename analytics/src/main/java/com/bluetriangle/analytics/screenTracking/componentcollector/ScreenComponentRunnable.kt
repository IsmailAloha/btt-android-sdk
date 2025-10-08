package com.bluetriangle.analytics.screenTracking.componentcollector

import androidx.core.net.toUri
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.Constants
import com.bluetriangle.analytics.Payload
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Utils
import com.bluetriangle.analytics.caching.classifier.CacheType
import com.bluetriangle.analytics.networkcapture.CapturedRequest
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_DURATION
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_FILE
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_START_TIME
import com.bluetriangle.analytics.screenTracking.componentcollector.ScreenComponent.Companion.ENTRY_TYPE
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal class ScreenComponentRunnable(
    private val configuration: BlueTriangleConfiguration,
    private val timerData: MostRecentTimerData,
    private val screenComponents: List<ScreenComponent>
) : Runnable {
    override fun run() {
        try {
            submitChildViews(screenComponents)
        } catch (e: Exception) {
            configuration.logger?.error("Error while submitting captured requests: ${e.message}")
        }
    }

    private fun submitChildViews(childViews: List<ScreenComponent>) {
        var connection: HttpsURLConnection? = null
        val payloadData = buildChildViewsData(childViews, if (configuration.isDebug) 2 else 0)
        var url = configuration.networkCaptureUrl
        try {
            url = timerData.buildUrl(configuration.networkCaptureUrl)
            configuration.logger?.debug("Submitting ${timerData.pageName} to $url")
            configuration.logger?.debug("${timerData.pageName} payload: $payloadData")
            val requestUrl = URL(url)
            connection = requestUrl.openConnection() as HttpsURLConnection
            connection.requestMethod = Constants.METHOD_POST
            connection.setRequestProperty(
                Constants.HEADER_CONTENT_TYPE,
                Constants.CONTENT_TYPE_JSON
            )
            connection.setRequestProperty(Constants.HEADER_USER_AGENT, configuration.userAgent)
            connection.doOutput = true
            DataOutputStream(connection.outputStream).use { it.write(Utils.b64encode(payloadData)) }
            val statusCode = connection.responseCode
            if (statusCode >= 300) {
                val responseBody =
                    BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
                configuration.logger?.error("Server Error submitting ${timerData.pageName}: $statusCode - $responseBody")

                // If server error, cache the payload and try again later
                if (statusCode >= 500) {
                    cachePayload(url, payloadData)
                }
            } else {
                configuration.logger?.debug("${timerData.pageName} submitted successfully")
            }
            connection.getHeaderField(0)
        } catch (e: Exception) {
            configuration.logger?.error(
                e,
                "Android Error submitting ${timerData.pageName}: ${e.message}"
            )
            cachePayload(url, payloadData)
        } finally {
            connection?.disconnect()
        }
    }

    private fun buildChildViewsData(childViews: List<ScreenComponent>, indentSpaces: Int): String {
        return JSONArray(childViews.map {
            JSONObject().apply {
                put(FIELD_FILE, it.className)
                put(FIELD_DURATION, it.pageTime)
                put(FIELD_START_TIME, it.startTime)
                put(CapturedRequest.FIELD_ENTRY_TYPE, ENTRY_TYPE)
                put(CapturedRequest.FIELD_END_TIME, it.endTime)
                put(CapturedRequest.FIELD_URL, it.pageName)
                put(Timer.FIELD_NATIVE_APP, it.nativeAppProperties.toJSONObject())
                it.performanceMetrics?.forEach { perf ->
                    put(perf.key, perf.value)
                }
            }
        }).toString(indentSpaces)
    }

    private fun MostRecentTimerData.buildUrl(baseUrl: String): String {
        return baseUrl.toUri().buildUpon()
            .appendQueryParameter(Timer.FIELD_SITE_ID, siteId)
            .appendQueryParameter(Timer.FIELD_NAVIGATION_START, nst.toString())
            .appendQueryParameter(Timer.FIELD_TRAFFIC_SEGMENT_NAME, trafficSegment)
            .appendQueryParameter(Timer.FIELD_LONG_SESSION_ID, sessionID)
            .appendQueryParameter(Timer.FIELD_PAGE_NAME, pageName)
            .appendQueryParameter(Timer.FIELD_CONTENT_GROUP_NAME, contentGroupName)
            .appendQueryParameter(Timer.FIELD_WCDTT, "c")
            .appendQueryParameter(Timer.FIELD_NATIVE_OS, Constants.OS)
            .appendQueryParameter(Timer.FIELD_BROWSER, Constants.BROWSER)
            .appendQueryParameter(Timer.FIELD_BROWSER_VERSION, browserVersion)
            .appendQueryParameter(Timer.FIELD_DEVICE, device)
            .build().toString()
    }

    /**
     * Cache the crash report to try and send again in the future
     * @param url URL to send to
     * @param payloadData payload data to send
     */
    private fun cachePayload(url: String, payloadData: String) {
        configuration.logger?.info("Caching network capture report")
        configuration.payloadCache?.save(
            Payload(
                url = url,
                data = payloadData,
                type = CacheType.Wcd,
                createdAt = System.currentTimeMillis()
            )
        )
    }
}
