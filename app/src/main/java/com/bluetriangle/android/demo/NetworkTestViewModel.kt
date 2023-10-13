package com.bluetriangle.android.demo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.okhttp.BlueTriangleOkHttpInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume

class NetworkTestViewModel : ViewModel() {

    private var timer: Timer? = null
    var isTimerStarted = MutableLiveData(false)
    private var okHttpClient: OkHttpClient? = null

    companion object {
        const val LIVE_API_URL = "https://hub.dummyapis.com/delay?seconds=3"
        const val NOT_FOUND_API_URL = "http://192.168.0.157/notfound"
        const val ERROR_404_API_URL = "https://d.btttag.com/not_found.rcv"
    }

    var displayMessageToUI: (String) -> Unit = {

    }

    init {
        Tracker.instance?.configuration?.let {
            okHttpClient = OkHttpClient.Builder()
                .addNetworkInterceptor(BlueTriangleOkHttpInterceptor(it))
                .build()
        }
    }

    fun onTimerStartStop() {
        if (timer == null) {
            timer = Timer("NetworkTestTimer", "NetworkTest")
            timer?.start()
        } else {
            timer?.submit()
            timer = null
        }
        isTimerStarted.value = isTimerStarted.value != true
    }

    fun onSuccessfulAPICallClick() {
        LIVE_API_URL.runAPICall("Live API")
    }

    fun on404ErrorAPIClick() {
        ERROR_404_API_URL.runAPICall("404 Error API")
    }

    fun onHostNotFoundAPIClick() {
        NOT_FOUND_API_URL.runAPICall("Host not found API")
    }

    private fun String.runAPICall(api:String) {
        viewModelScope.launch(Dispatchers.IO) {
            this@runAPICall.makeAPICall()
            withContext(Dispatchers.Main) {
                displayMessageToUI("$api Done")
            }
        }
    }

    suspend fun String.makeAPICall(): Unit = suspendCancellableCoroutine { continuation ->
        val client = okHttpClient ?: return@suspendCancellableCoroutine
        val request = Request.Builder()
            .url(this)
            .build()
        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                log("Exception in API call: ${this@makeAPICall} : ${e.message}")
                continuation.resume(Unit)
            }

            override fun onResponse(call: Call, response: Response) {
                log("API call successful: ${this@makeAPICall} : ${response.code}")
                continuation.resume(Unit)
            }
        })
    }

    fun log(message: String) {
        Tracker.instance?.configuration?.logger?.debug("NetworkTestViewModel : $message")
    }
}