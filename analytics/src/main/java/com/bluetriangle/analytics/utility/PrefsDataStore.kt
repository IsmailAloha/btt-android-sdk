// SharedPreferencesManager.kt
package com.bluetriangle.analytics.utility

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PrefsDataStore {

    private const val DEFAULT_PREFS_NAME = "BTT_SHARED_PREFERENCES"
    private const val GLOBAL_USER_ID = "globalUserID"
    private const val CONFIG_KEY = "configKey"

    private var prefs: SharedPreferences? = null

    fun init(context: Context, name: String = DEFAULT_PREFS_NAME, mode: Int = Context.MODE_PRIVATE) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(name, mode)
        }
    }

    fun getGlobalUserId():String? {
        return getString(GLOBAL_USER_ID)
    }

    fun setGlobalUserId(globalUserId: String) {
        putString(GLOBAL_USER_ID, globalUserId)
    }

    fun setConfigKey(configID: String) {
        putString(CONFIG_KEY, configID)
    }

    fun getConfigKey(): String? {
        return getString(CONFIG_KEY)
    }

    fun clearConfigKey() {
        remove(CONFIG_KEY)
    }

    private fun getString(key: String) = prefs?.getString(key, null)

    private fun putString(key: String, value: String) = prefs?.edit {
        putString(key, value)
    }

    private fun remove(key: String) = prefs?.edit {
        remove(key)
    }
}
