package com.bluetriangle.analytics.thirdpartyintegration

internal class ThirdPartyConnectorManager {
    private var connectors = mutableListOf<ThirdPartyConnector>()

    val payloadFields: Map<String, String?>
        get() = buildMap {
            connectors.forEach {
                putAll(it.payloadFields())
            }
        }

    val nativeAppPayloadFields: Map<String, String?>
        get() = buildMap {
            connectors.forEach {
                putAll(it.nativeAppPayloadFields())
            }
        }

    fun register(connector: ThirdPartyConnector) {
        connectors.add(connector)
    }

    fun startConnectors() {
        connectors.forEach { it.start() }
    }

    fun setConfiguration(config: ConnectorConfiguration) {
        connectors.forEach {
            it.setConfiguration(config)
        }
    }

    fun stopConnectors() {
        connectors.forEach { it.stop() }
    }

}