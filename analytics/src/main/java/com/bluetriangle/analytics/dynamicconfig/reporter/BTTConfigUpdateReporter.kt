/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.reporter

import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.CrashRunnable
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.deviceinfo.IDeviceInfoProvider
import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration
import com.bluetriangle.analytics.utility.PrefsDataStore

internal class BTTConfigUpdateReporter(
    val configuration: BlueTriangleConfiguration,
    private val deviceInfoProvider: IDeviceInfoProvider
) : IBTTConfigUpdateReporter {

    companion object {
        private const val CONFIG_UPDATE_PAGE_NAME = "BTTConfigUpdate"
    }

    override fun reportSuccess(config: BTTRemoteConfiguration) {
        config.configKey?.apply {
            PrefsDataStore.setConfigKey(this)
        }
    }

    override fun reportError(error: BTTConfigFetchError) {
        val crashHitsTimer: Timer = Timer().startWithoutPerformanceMonitor()

        crashHitsTimer.setPageName(CONFIG_UPDATE_PAGE_NAME)
        crashHitsTimer.setTrafficSegmentName(CONFIG_UPDATE_PAGE_NAME)
        crashHitsTimer.setContentGroupName(CONFIG_UPDATE_PAGE_NAME)
        crashHitsTimer.nativeAppProperties.add(deviceInfoProvider.getDeviceInfo())
        crashHitsTimer.setError(true)

        try {
            val thread = Thread(
                CrashRunnable(
                    configuration,
                    "${Tracker.BTErrorType.BTTConfigUpdateError.value} : ${error.reason}",
                    System.currentTimeMillis().toString(),
                    crashHitsTimer,
                    Tracker.BTErrorType.BTTConfigUpdateError,
                    deviceInfoProvider = deviceInfoProvider
                )
            )
            thread.start()
            thread.join()
        } catch (interruptedException: InterruptedException) {
            interruptedException.printStackTrace()
        }
    }
}