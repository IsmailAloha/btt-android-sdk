package com.bluetriangle.analytics.common

import android.app.Application
import java.lang.ref.WeakReference

object SDKEventsManager {

    private val sdkEventsListeners = arrayListOf<WeakReference<SDKEventsListener>>()

    fun registerConfigurationEventListener(listener: SDKEventsListener) {
        sdkEventsListeners.add(WeakReference(listener))
    }

    fun unregisterConfigurationEventListener(listener: SDKEventsListener) {
        sdkEventsListeners.removeIf { it.get() == listener }
    }
    private var application: Application ?= null

    fun notifyConfigured(application: Application, sdkConfiguration: SDKConfiguration) {
        this.application = application

        sdkEventsListeners.forEach {
            it.get()?.onConfigured(application, sdkConfiguration)
        }
    }

    fun notifyEnabled(sdkConfiguration: SDKConfiguration) {
        sdkEventsListeners.forEach {
            it.get()?.onEnabled(sdkConfiguration)
        }
    }

    fun notifyDisabled(sdkConfiguration: SDKConfiguration) {
        sdkEventsListeners.forEach {
            it.get()?.onDisabled(sdkConfiguration)
        }
    }

    fun notifySessionChanged(sdkConfiguration: SDKConfiguration) {
        sdkEventsListeners.forEach {
            it.get()?.onSessionChanged(sdkConfiguration)
        }
    }
}