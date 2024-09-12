package com.bluetriangle.analytics.launchtime

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal interface AppEventConsumer {

    fun onAppCreated(application: Application) {

    }

    fun onActivityCreated(activity: Activity, data: Bundle?) {

    }

    fun onActivityStarted(activity: Activity) {

    }

    fun onActivityResumed(activity: Activity) {

    }

    fun onAppMovedToBackground(application:Application) {

    }

}