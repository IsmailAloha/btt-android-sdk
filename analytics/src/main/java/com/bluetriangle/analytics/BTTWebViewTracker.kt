package com.bluetriangle.analytics

import android.os.Build
import android.webkit.WebView

object BTTWebViewTracker {

    @JvmStatic
    public fun onLoadResource(view: WebView?, url: String?) {
        if (view == null || url == null) return
        val fileName = url.split("/").lastOrNull { segment -> segment.isNotEmpty() }
        if (fileName != "btt.js") return

        Tracker.instance?.configuration?.sessionId?.let {
            val expiration = (System.currentTimeMillis() + 1800000).toString()
            val sessionID = "{\"value\":\"$it\",\"expires\":\"$expiration\"}"
            val sdkVersion = "{\"value\":\"Android-${BuildConfig.SDK_VERSION}\",\"expires\":\"$expiration\"}"

            view.runJS("window.localStorage.setItem('BTT_X0siD', '$sessionID');")
            view.runJS("window.localStorage.setItem('BTT_SDK_VER', '$sdkVersion');")
            Tracker.instance?.configuration?.logger?.info("Injected session ID and SDK version in WebView: BTT_X0siD: $sessionID, BTT_SDK_VER: $sdkVersion")
        }
    }

    private fun WebView.runJS(js: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(js, null)
        } else {
            loadUrl("javascript:$js")
        }
    }

}