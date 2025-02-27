package com.bluetriangle.analytics.networkstate

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.networkstate.data.BTTNetworkProtocol
import com.bluetriangle.analytics.networkstate.networkprotocol.NetworkProtocolProvider
import com.bluetriangle.analytics.networkstate.networkprotocol.NetworkProtocolProviderFactory
import com.bluetriangle.analytics.utility.cellularTransports
import com.bluetriangle.analytics.utility.ethernetTransports
import com.bluetriangle.analytics.utility.wifiTransports
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


internal sealed class BTTNetworkState {
    object Wifi : BTTNetworkState()
    class Cellular(val source: String, val protocol: BTTNetworkProtocol) : BTTNetworkState()
    object Ethernet : BTTNetworkState()
    object Other : BTTNetworkState()
    object Offline : BTTNetworkState()
}

internal interface INetworkStateMonitor {
    val state: StateFlow<BTTNetworkState>

    fun stop()
}

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal class NetworkStateMonitor(logger: Logger?, context: Context) : INetworkStateMonitor {

    private val connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager

    private var networkProtocolProvider: NetworkProtocolProvider = NetworkProtocolProviderFactory.getNetworkProtocolProvider(context)

    private val wifiTransportHandler = NetworkTransportHandler(
        wifiTransports, logger, BTTNetworkState.Wifi
    )

    private val cellularTransportHandler = NetworkTransportHandler(
        cellularTransports, logger, BTTNetworkState.Cellular("", BTTNetworkProtocol.Unknown)
    )

    private val ethernetTransportHandler = NetworkTransportHandler(
        ethernetTransports, logger, BTTNetworkState.Ethernet
    )

    /**
     * The arguments in the combine are based on their priority. If multiple network transports are available,
     * then the one that is first in this list gets more priority than the ones after it.
     * e.g. if wifi and cellular are both connected, we say the current network state is Wifi.
     */
    override val state: StateFlow<BTTNetworkState> = combine(
        wifiTransportHandler.isConnected,
        cellularTransportHandler.isConnected,
        ethernetTransportHandler.isConnected,
        networkProtocolProvider.networkProtocol
    ) { wifi, cellular, ethernet, protocol ->
        when {
            wifi -> BTTNetworkState.Wifi
            ethernet -> BTTNetworkState.Ethernet
            cellular -> BTTNetworkState.Cellular(protocol.source?.toString()?:"", protocol.protocol)
            else -> BTTNetworkState.Offline
        }
    }.stateIn(GlobalScope, SharingStarted.WhileSubscribed(), BTTNetworkState.Offline)

    private val handlers = arrayOf(wifiTransportHandler, cellularTransportHandler, ethernetTransportHandler)
    init {
        handlers.forEach {
            connectivityManager?.registerNetworkCallback(
                it.networkRequest, it.listener
            )
        }
        logger?.debug("-------------------Network Monitoring Started!-----------------")
    }

    override fun stop() {
        handlers.forEach {
            connectivityManager?.unregisterNetworkCallback(it.listener)
        }
    }
}