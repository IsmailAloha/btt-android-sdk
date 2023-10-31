package com.bluetriangle.analytics.model

import android.os.Parcelable
import com.bluetriangle.analytics.networkcapture.CapturedRequest
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
internal data class NativeAppProperties(
    var loadTime:Long?=null,
    var fullTime:Long?=null,
    var maxMainThreadUsage:Long?=null,
    var screenType:ScreenType?=null,
    var numberOfCPUCores:Long?=null,
    var wifi: Long? = null,
    var cellular: Long? = null,
    var ethernet: Long? = null,
    var offline: Long? = null
):Parcelable {
    fun toJSONObject(): JSONObject {
        val obj = JSONObject()
        obj.put("loadTime", loadTime)
        obj.put("fullTime", fullTime)
        obj.put("maxMainThreadUsage", maxMainThreadUsage)
        obj.put("screenType", screenType?.name)
        obj.put("numberOfCPUCores", numberOfCPUCores)
        var max = Long.MIN_VALUE
        var maxField = ""
        if (wifi != null && wifi != 0L) {
            obj.put("wifi", wifi)
            if(wifi!! > max) {
                max = wifi!!
                maxField = "wifi"
            }
        }
        if (cellular != null && cellular != 0L) {
            obj.put("cellular", cellular)
            if(cellular!! > max) {
                max = cellular!!
                maxField = "cellular"
            }
        }
        if (ethernet != null && ethernet != 0L) {
            obj.put("ethernet", ethernet)
            if(ethernet!! > max) {
                max = ethernet!!
                maxField = "ethernet"
            }
        }
        if (offline != null && offline != 0L) {
            obj.put("offline", offline)
            if(offline!! > max) {
                max = offline!!
                maxField = "offline"
            }
        }
        if(max != Long.MIN_VALUE) {
            obj.put(CapturedRequest.FIELD_NETWORK_STATE, maxField)
        }
        return obj
    }
}