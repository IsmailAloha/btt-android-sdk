package com.bluetriangle.analytics.screenTracking.componentcollector

data class MostRecentTimerData(
    val siteId: String?,
    val nst: Long,
    val trafficSegment: String?,
    val contentGroupName: String?,
    val sessionID: String?,
    val pageName: String?,
    val browserVersion: String?,
    val device: String?
)