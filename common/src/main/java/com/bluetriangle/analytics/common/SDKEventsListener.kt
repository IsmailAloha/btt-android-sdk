package com.bluetriangle.analytics.common

interface SDKEventsListener {

    fun onConfigured(configuration: SDKConfiguration)

    fun onEnabled(configuration: SDKConfiguration)

    fun onDisabled(configuration: SDKConfiguration)

    fun onSessionChanged(configuration: SDKConfiguration)

}