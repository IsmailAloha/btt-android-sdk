package com.bluetriangle.analytics.utility

import org.json.JSONObject

internal fun JSONObject.getIntOrNull(key:String):Int? {
    return if(has(key)) getInt(key) else null
}

internal fun JSONObject.getDoubleOrNull(key:String):Double? {
    return if(has(key)) getDouble(key) else null
}

internal fun JSONObject.getLongOrNull(key:String):Long? {
    return if(has(key)) getLong(key) else null
}

internal fun JSONObject.getBooleanOrNull(key:String):Boolean? {
    return if(has(key)) getBoolean(key) else null
}
