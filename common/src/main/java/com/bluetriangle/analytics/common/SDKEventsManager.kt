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
        val app = this.application?:return

        sdkEventsListeners.forEach {
            it.get()?.onEnabled(app, sdkConfiguration)
        }
    }

    fun notifyDisabled(sdkConfiguration: SDKConfiguration) {
        val app = this.application?:return

        sdkEventsListeners.forEach {
            it.get()?.onDisabled(app, sdkConfiguration)
        }
    }

    fun notifySessionChanged(sdkConfiguration: SDKConfiguration) {
        val app = this.application?:return

        sdkEventsListeners.forEach {
            it.get()?.onSessionChanged(app, sdkConfiguration)
        }
    }
}