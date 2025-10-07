/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.reporter

import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration

internal interface IBTTConfigUpdateReporter {

    fun reportSuccess(config: BTTRemoteConfiguration)

    fun reportError(error: BTTConfigFetchError)

}