package com.bluetriangle.analytics.model

import android.os.Parcelable
import com.bluetriangle.analytics.launchtime.LaunchTimeMonitor
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
internal data class NativeAppProperties(
    var loadTime:Long?=null,
    var fullTime:Long?=null,
    var maxMainThreadUsage:Long?=null,
    var screenType:ScreenType?=null,
    var numberOfCPUCores:Long?=null
):Parcelable {
    fun toJSONObject(): JSONObject {
        val obj = JSONObject()
        obj.put("loadTime", loadTime)
        obj.put("fullTime", fullTime)
        obj.put("maxMainThreadUsage", maxMainThreadUsage)
        obj.put("screenType", screenType?.name)
        obj.put("numberOfCPUCores", numberOfCPUCores)
        return obj
    }
}