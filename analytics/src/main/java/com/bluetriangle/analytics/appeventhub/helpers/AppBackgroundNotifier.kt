package com.bluetriangle.analytics.appeventhub.helpers

import android.app.Application
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bluetriangle.analytics.appeventhub.AppEventHub

internal class AppBackgroundNotifier(val application: Application):DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        AppEventHub.instance.onAppMovedToBackground(application)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        AppEventHub.instance.onAppDestroyed(application)
    }
}
