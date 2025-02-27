/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.sessionmanager

import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.utility.getBooleanOrNull
import com.bluetriangle.analytics.utility.getDoubleOrNull
import com.bluetriangle.analytics.utility.getJsonArrayOrNull
import com.bluetriangle.analytics.utility.getStringOrNull
import org.json.JSONArray
import org.json.JSONObject

internal data class SessionData(
    val sessionId: String,
    val shouldSampleNetwork: Boolean,
    val isConfigApplied: Boolean,
    val networkSampleRate: Double,
    val ignoreScreens: List<String>,
    val expiration: Long
) {
    companion object {
        private const val SESSION_ID = "sessionId"
        private const val EXPIRATION = "expiration"
        private const val SHOULD_SAMPLE_NETWORK = "shouldSampleNetwork"
        private const val IS_CONFIG_APPLIED = "isConfigApplied"
        private const val NETWORK_SAMPLE_RATE = "networkSampleRate"
        private const val IGNORE_SCREENS = "ignoreScreens"

        internal fun JSONObject.toSessionData(): SessionData? {
            try {
                return SessionData(
                    sessionId = getStringOrNull(SESSION_ID)?:return null,
                    shouldSampleNetwork = getBooleanOrNull(SHOULD_SAMPLE_NETWORK)?:false,
                    isConfigApplied = getBooleanOrNull(IS_CONFIG_APPLIED)?:false,
                    networkSampleRate = getDoubleOrNull(NETWORK_SAMPLE_RATE)?:0.0,
                    ignoreScreens = getJsonArrayOrNull(IGNORE_SCREENS)?.let { array ->
                        buildList {
                            repeat(array.length()) {
                                add(array.getString(it))
                            }
                        }
                    } ?: listOf(),
                    expiration = getLong(EXPIRATION)
                )
            } catch (e: Exception) {
                Tracker.instance?.configuration?.logger?.error("Error while parsing session data: ${e::class.simpleName}(\"${e.message}\")")
                return null
            }
        }

        internal fun SessionData.toJsonObject() = JSONObject().apply {
            put(SESSION_ID, sessionId)
            put(SHOULD_SAMPLE_NETWORK, shouldSampleNetwork)
            put(IS_CONFIG_APPLIED, isConfigApplied)
            put(NETWORK_SAMPLE_RATE, networkSampleRate)
            put(IGNORE_SCREENS, JSONArray(ignoreScreens))
            put(EXPIRATION, expiration)
        }
    }
}