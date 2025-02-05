package com.bluetriangle.analytics.clarity

import android.app.Application
import android.util.Log
import com.bluetriangle.analytics.common.SDKConfiguration
import com.bluetriangle.analytics.common.SDKEventsListener
import com.bluetriangle.analytics.common.SDKEventsManager

object ClarityInitializer:SDKEventsListener {

    init {
        SDKEventsManager.registerConfigurationEventListener(this)
    }

    override fun onConfigured(application: Application, configuration: SDKConfiguration) {
        Log.d("BlueTriangle", "ClarityInitializer::onEnabled")
    }

    override fun onEnabled(application: Application, configuration: SDKConfiguration) {
        Log.d("BlueTriangle", "ClarityInitializer::onEnabled")
    }

    override fun onDisabled(application: Application, configuration: SDKConfiguration) {
        Log.d("BlueTriangle", "ClarityInitializer::onDisabled")
    }

    override fun onSessionChanged(application: Application, configuration: SDKConfiguration) {
        Log.d("BlueTriangle", "ClarityInitializer::onSessionChanged")
    }

}