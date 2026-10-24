package com.privatereelstracker.util

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {

    private const val PREFS_NAME = "reel_tracker_prefs"
    private const val KEY_LOCK_AT_100 = "lock_at_100_reels"
    private const val KEY_LOCK_ENABLED = "lock_feature_enabled"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isLockEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_LOCK_ENABLED, false)
    }

    fun setLockEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_LOCK_ENABLED, enabled).apply()
    }
}
