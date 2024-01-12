package com.bluetriangle.analytics

import android.os.Build
import android.webkit.WebView

object BTTWebViewTracker {

    @JvmStatic
    public fun onLoadResource(view: WebView?, url: String?) {
        if(view == null || url == null) return
        val fileName = url.split("/").lastOrNull { segment -> segment.isNotEmpty() }
        if(fileName != "btt.js") return

        Tracker.instance?.configuration?.sessionId?.let {
            val expiration = (System.currentTimeMillis() + 1800000).toString()
            val setSession = "{\"value\":\"$it\",\"expires\":\"$expiration\"}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view.evaluateJavascript(
                    "window.localStorage.setItem('BTT_X0siD', '$setSession');"
                ) {
                    Tracker.instance?.configuration?.logger?.info("Injected SessionID in WebView: $setSession, $it")
                }
            } else {
                view.loadUrl("javascript:window.localStorage.setItem('BTT_X0siD', '$setSession');")
            }
        }
    }

}