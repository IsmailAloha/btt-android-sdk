package com.bluetriangle.android.demo

import android.view.View
import com.bluetriangle.analytics.Tracker
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class NetworkPocHandler(private val okHttpClient: OkHttpClient) : Callback {

    private val logger = Tracker.instance?.configuration?.logger

    fun failingAtDns(view: View) {
        val dnsFailRequest = Request.Builder()
            .url("https://dnsrequestwouldfailhere.com")
            .build()
        makeRequest(dnsFailRequest)
    }

    fun failingAtConnection(view: View) {
        val connectionFailRequest = Request.Builder()
            .url("https://192.169.2.111/hostnotfound")
            .build()
        makeRequest(connectionFailRequest)
    }

    fun failingAtRequest(view: View) {
        val uploadFailRequest = Request.Builder()
            .url("http://192.168.0.107:3000")
            .header("test", "test_value")
            .post("{\"data\":\"Some random data to be sent to server\"}".toRequestBody("application/json".toMediaType()))
            .build()
        makeRequest(uploadFailRequest)
    }

    fun failingAtResponse(view: View) {
        val downloadFailRequest = Request.Builder()
            .url("http://192.168.0.107:3000")
            .build()
        makeRequest(downloadFailRequest)
    }

    fun failingAtTLS(view: View) {
        val tlsFailRequest = Request.Builder()
            .url("https://untrusted-root.badssl.com/")
            .build()
        makeRequest(tlsFailRequest)
    }

    private fun makeRequest(request: Request) {
        okHttpClient.newCall(request)
            .enqueue(this)
    }

    override fun onFailure(call: Call, e: IOException) {
        logger?.error("Received failure for call ${call.request().url}, reason: ${e::class.java.simpleName}(\"${e.message}\")")
    }

    override fun onResponse(call: Call, response: Response) {
        response.body?.close()
        logger?.error("Received response for call ${call.request().url}, responseCode: ${response.code}")
    }
}