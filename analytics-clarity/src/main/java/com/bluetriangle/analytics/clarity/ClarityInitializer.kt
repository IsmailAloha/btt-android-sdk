package com.bluetriangle.analytics.clarity

import android.app.Application
import android.util.Log
import com.bluetriangle.analytics.sdkevents.SDKConfiguration
import com.bluetriangle.analytics.sdkevents.SDKEventsListener
import com.bluetriangle.analytics.sdkevents.SDKEventsManager

object ClarityInitializer: SDKEventsListener {

    init {
        SDKEventsManager.registerConfigurationEventListener(this)
    }

    override fun onConfigured(application: Application, configuration: SDKConfiguration) {
        Log.d("BlueTriangle", "ClarityInitializer::onEnabled::${configuration}")
    }

    override fun onEnabled(application: Application, configuration: SDKConfiguration) {
        Log.d("BlueTriangle", "ClarityInitializer::onEnabled::${configuration}")
    }

    override fun onDisabled(application: Application, configuration: SDKConfiguration) {
        Log.d("BlueTriangle", "ClarityInitializer::onDisabled::${configuration}")
    }

    override fun onSessionChanged(application: Application, configuration: SDKConfiguration) {
        Log.d("BlueTriangle", "ClarityInitializer::onSessionChanged::${configuration}")
    }

}