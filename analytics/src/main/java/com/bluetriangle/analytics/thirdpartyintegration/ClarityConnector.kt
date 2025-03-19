package com.bluetriangle.analytics.thirdpartyintegration

import android.app.Application
import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.isClassAvailable
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel

internal class ClarityConnector(val application: Application,
                                logger: Logger?,
                                customVariablesAdapter: CustomVariablesAdapter
): ThirdPartyConnector(logger, customVariablesAdapter) {

    private var clarityProjectID: String? = null
    private var clarityEnabled: Boolean = false

    companion object {
        const val CLARITY_PROJECT_ID = "clarityProjectID"
        const val CLARITY_SESSION_URL_CV = "CV0"
    }

    private var isClarityAvailable = isClassAvailable("com.microsoft.clarity.Clarity")

    @Synchronized
    override fun start(connectorConfiguration: ConnectorConfiguration) {
        if(!isClarityAvailable) return

        clarityProjectID = connectorConfiguration.clarityProjectID
        clarityEnabled = connectorConfiguration.clarityEnabled

        Clarity.setOnSessionStartedCallback {
            logger?.debug("Clarity session started: ${Clarity.getCurrentSessionUrl()}")
            setSessionURLToCustomVariable()
        }
        clarityProjectID?.also {
            if(Clarity.isPaused()) {
                logger?.debug("Clarity is paused, resuming")
                Clarity.resume()
                setSessionURLToCustomVariable()
            } else if (clarityEnabled) {
                logger?.debug("Clarity initialized for project ID: $it")
                Clarity.initialize(application, ClarityConfig(it, logLevel = LogLevel.Verbose))
            }
        }
    }

    private fun setSessionURLToCustomVariable() {
        if(!isClarityAvailable) return

        val sessionURL = Clarity.getCurrentSessionUrl()
        if(sessionURL != null) {
            customVariablesAdapter.setCustomVariable(CLARITY_SESSION_URL_CV, sessionURL)
        }
    }

    @Synchronized
    override fun stop() {
        if(!isClarityAvailable) return

        Clarity.pause()
        customVariablesAdapter.clearCustomVariable(CLARITY_SESSION_URL_CV)
        logger?.debug("Clarity paused")
    }

    @Synchronized
    override fun nativeAppPayloadFields() = if(clarityProjectID != null && clarityEnabled && isClarityAvailable) {
        mapOf(
            CLARITY_PROJECT_ID to clarityProjectID
        )
    } else {
        mapOf()
    }

    @Synchronized
    override fun payloadFields() = mapOf<String, String>()

}