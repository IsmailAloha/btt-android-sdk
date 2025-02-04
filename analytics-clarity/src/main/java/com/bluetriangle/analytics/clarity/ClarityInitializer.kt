package com.bluetriangle.analytics.clarity

import com.bluetriangle.analytics.common.SDKConfiguration
import com.bluetriangle.analytics.common.SDKEventsListener
import com.bluetriangle.analytics.common.SDKEventsManager
import com.microsoft.clarity.Clarity

object ClarityInitializer:SDKEventsListener {

    init {
        SDKEventsManager.registerConfigurationEventListener(this)
    }

    override fun onConfigured(configuration: SDKConfiguration) {
        configuration.clarityProjectID?.let {

        }
    }

    override fun onEnabled(configuration: SDKConfiguration) {

    }

    override fun onDisabled(configuration: SDKConfiguration) {

    }

    override fun onSessionChanged(configuration: SDKConfiguration) {

    }

}