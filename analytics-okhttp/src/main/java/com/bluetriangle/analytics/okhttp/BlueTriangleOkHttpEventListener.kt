package com.bluetriangle.analytics.okhttp

import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.networkcapture.CapturedRequest
import com.bluetriangle.analytics.networkcapture.NetworkNativeAppProperties
import okhttp3.Call
import okhttp3.Connection
import okhttp3.EventListener
import okhttp3.Handshake
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy

class BlueTriangleOkHttpEventListener(private val configuration: BlueTriangleConfiguration) : EventListener() {

    private val logger = configuration.logger

    companion object {
        private const val TAG = "OkHttpEventListener"
    }

    override fun callStart(call: Call) {
        super.callStart(call)
        logger?.info("$TAG: 1.1. callStart(${call.request().url})")
    }

    override fun callFailed(call: Call, ioe: IOException) {
        super.callFailed(call, ioe)
        logger?.info("$TAG: 1.2. callFailed(${call.request().url}, ${ioe::class.java.simpleName}(\"${ioe.message}\"))")
        if (!configuration.shouldSampleNetwork) {
            configuration.logger?.error("Not sampling network")
            return
        }
        val capturedRequest = CapturedRequest()
        capturedRequest.start()
        capturedRequest.url = call.request().url.toString()
        val mediaType = call.request().headers["Content-Type"]?.toMediaType()
        capturedRequest.requestType = requestTypeFromMediaType(capturedRequest.file, mediaType)
        capturedRequest.encodedBodySize = call.request().body?.contentLength()?:0
        capturedRequest.responseStatusCode = 600
        capturedRequest.nativeAppProperties = NetworkNativeAppProperties("${ioe::class.java.simpleName} : ${ioe.message}")
        capturedRequest.stop()
        capturedRequest.submit()
        configuration.logger?.debug("Submitted request: ${capturedRequest.url}, ${capturedRequest.duration}")
    }

    override fun callEnd(call: Call) {
        super.callEnd(call)
        logger?.info("$TAG: 1.3. callEnd(${call.request().url})")
    }

    override fun dnsStart(call: Call, domainName: String) {
        super.dnsStart(call, domainName)
        logger?.info("$TAG: 2.1. dnsStart(${call.request().url}, $domainName)")
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
        super.dnsEnd(call, domainName, inetAddressList)
        logger?.info("$TAG: 2.2. dnsEnd(${call.request().url}, $domainName, $inetAddressList)")
    }

    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
        super.connectStart(call, inetSocketAddress, proxy)
        logger?.info("$TAG: 3.1. connectStart(${call.request().url}, $inetSocketAddress, $proxy)")
    }

    override fun connectionAcquired(call: Call, connection: Connection) {
        super.connectionAcquired(call, connection)
        logger?.info("$TAG: 3.2. connectionAcquired(${call.request().url}, $connection)")
    }

    override fun connectionReleased(call: Call, connection: Connection) {
        super.connectionReleased(call, connection)
        logger?.info("$TAG: 3.3. connectionReleased(${call.request().url}, $connection)")
    }

    override fun connectFailed(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?,
        ioe: IOException
    ) {
        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe)
        logger?.info("$TAG: 3.4. connectFailed($${call.request().url}, $inetSocketAddress, $proxy, $protocol, $${ioe::class.java.simpleName}(\"${ioe.message}\"))")

    }

    override fun connectEnd(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?
    ) {
        super.connectEnd(call, inetSocketAddress, proxy, protocol)
        logger?.info("$TAG: 3.5. connectEnd(${call.request().url}, $inetSocketAddress, $proxy, $protocol)")
    }

    override fun requestHeadersStart(call: Call) {
        super.requestHeadersStart(call)
        logger?.info("$TAG: 4.1. requestHeadersStart(${call.request().url})")
    }

    override fun requestHeadersEnd(call: Call, request: Request) {
        super.requestHeadersEnd(call, request)
        logger?.info("$TAG: 4.2. requestHeadersEnd(${call.request().url}, ${request.body?.contentLength()})")
    }

    override fun requestBodyStart(call: Call) {
        super.requestBodyStart(call)
        logger?.info("$TAG: 4.3. requestBodyStart(${call.request().url})")
    }

    override fun requestBodyEnd(call: Call, byteCount: Long) {
        super.requestBodyEnd(call, byteCount)
        logger?.info("$TAG: 4.4. requestBodyEnd(${call.request().url}, $byteCount)")
    }

    override fun requestFailed(call: Call, ioe: IOException) {
        super.requestFailed(call, ioe)
        logger?.info("$TAG: 4.5. requestFailed(${call.request().url}, ${ioe::class.java.simpleName}(\"${ioe.message}\"))")
    }

    override fun responseHeadersStart(call: Call) {
        super.responseHeadersStart(call)
        logger?.info("$TAG: 5.1. responseHeadersStart(${call.request().url})")
    }

    override fun responseHeadersEnd(call: Call, response: Response) {
        super.responseHeadersEnd(call, response)
        logger?.info("$TAG: 5.2. responseHeadersEnd(${call.request().url}, ${response.code})")
    }

    override fun responseBodyStart(call: Call) {
        super.responseBodyStart(call)
        logger?.info("$TAG: 5.3. responseBodyStart(${call.request().url})")
    }

    override fun responseBodyEnd(call: Call, byteCount: Long) {
        super.responseBodyEnd(call, byteCount)
        logger?.info("$TAG: 5.4. responseBodyEnd(${call.request().url}, $byteCount)")
    }

    override fun responseFailed(call: Call, ioe: IOException) {
        super.responseFailed(call, ioe)
        logger?.info("$TAG: 5.5. responseFailed(${call.request().url}, ${ioe::class.java.simpleName}(\"${ioe.message}\"))")
    }

    override fun secureConnectStart(call: Call) {
        super.secureConnectStart(call)
        logger?.info("$TAG: 6.1. secureConnectStart(${call.request().url})")
    }

    override fun secureConnectEnd(call: Call, handshake: Handshake?) {
        super.secureConnectEnd(call, handshake)
        logger?.info("$TAG: 6.2. secureConnectEnd(${call.request().url}, $handshake)")
    }

}